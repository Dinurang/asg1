package com.suntravels.search;

import com.suntravels.contract.HotelContract;
import com.suntravels.contract.HotelContractRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchService {
    private final HotelContractRepository repository;

    public SearchService(HotelContractRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<SearchResult> search(SearchRequest request) {
        var lastNight = request.checkIn().plusDays(request.nights() - 1L);
        int rooms = request.rooms().size();
        int adults = request.rooms().stream().mapToInt(SearchRequest.RoomRequest::adults).sum();
        return repository.findByValidFromLessThanEqualAndValidToGreaterThanEqual(
                        request.checkIn(), lastNight)
                .stream()
                .flatMap(contract -> contract.getRoomTypes().stream()
                        .map(room -> result(contract, room, request, rooms, adults)))
                .toList();
    }

    private SearchResult result(HotelContract contract, com.suntravels.contract.RoomType room,
                                SearchRequest request, int rooms, int adults) {
        boolean fitsOccupancy = request.rooms().stream()
                .allMatch(r -> r.adults() <= room.getMaxAdults());
        boolean available = fitsOccupancy && room.getRooms() >= rooms;
        var price = room.getPricePerPersonPerNight()
                .multiply(BigDecimal.ONE.add(contract.getMarkupPercent()
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                .multiply(BigDecimal.valueOf(request.nights()))
                .multiply(BigDecimal.valueOf(adults))
                .setScale(2, RoundingMode.HALF_UP);
        return new SearchResult(contract.getId(), contract.getHotelName(), room.getName(),
                price, available, rooms, adults);
    }
}
