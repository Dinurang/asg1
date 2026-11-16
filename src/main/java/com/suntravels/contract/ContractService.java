package com.suntravels.contract;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractService {
    private final HotelContractRepository repository;

    public ContractService(HotelContractRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<HotelContract> findAll() {
        return repository.findAll();
    }

    @Transactional
    public HotelContract create(ContractRequest request) {
        validateDates(request);
        var rooms = request.roomTypes().stream()
                .map(r -> new RoomType(r.name(), r.pricePerPersonPerNight(), r.rooms(), r.maxAdults()))
                .toList();
        return repository.save(new HotelContract(request.hotelName(), request.validFrom(),
                request.validTo(), request.markupPercent(), rooms));
    }

    @Transactional
    public HotelContract update(Long id, ContractRequest request) {
        validateDates(request);
        var contract = repository.findById(id)
                .orElseThrow(() -> new ContractNotFoundException(id));
        var rooms = request.roomTypes().stream()
                .map(r -> new RoomType(r.name(), r.pricePerPersonPerNight(), r.rooms(), r.maxAdults()))
                .toList();
        contract.update(request.hotelName(), request.validFrom(), request.validTo(),
                request.markupPercent(), rooms);
        return repository.save(contract);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ContractNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private void validateDates(ContractRequest request) {
        if (request.validTo().isBefore(request.validFrom())) {
            throw new InvalidContractException("Contract end date must not be before its start date");
        }
    }
}
