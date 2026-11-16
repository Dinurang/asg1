package com.suntravels.contract;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelContractRepository extends JpaRepository<HotelContract, Long> {
    List<HotelContract> findByValidFromLessThanEqualAndValidToGreaterThanEqual(
            LocalDate checkIn, LocalDate checkOut);
}
