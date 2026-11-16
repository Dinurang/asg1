package com.suntravels.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.suntravels.contract.HotelContract;
import com.suntravels.contract.HotelContractRepository;
import com.suntravels.contract.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {
    @Mock
    private HotelContractRepository repository;

    @Test
    void calculatesMarkedUpPriceForAllAdultsAndNights() {
        var contract = new HotelContract("Hilton Colombo", LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31), BigDecimal.valueOf(15),
                List.of(new RoomType("Standard", BigDecimal.valueOf(35), 15, 2)));
        when(repository.findByValidFromLessThanEqualAndValidToGreaterThanEqual(
                LocalDate.of(2026, 12, 30), LocalDate.of(2026, 12, 31)))
                .thenReturn(List.of(contract));

        var result = new SearchService(repository).search(new SearchRequest(
                LocalDate.of(2026, 12, 30), 2,
                List.of(new SearchRequest.RoomRequest(2))));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().price()).isEqualByComparingTo("161.00");
        assertThat(result.getFirst().available()).isTrue();
    }

    @Test
    void marksRoomUnavailableWhenInventoryCannotSatisfyRequestedRooms() {
        var contract = new HotelContract("Small Hotel", LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31), BigDecimal.valueOf(15),
                List.of(new RoomType("Standard", BigDecimal.TEN, 1, 2)));
        when(repository.findByValidFromLessThanEqualAndValidToGreaterThanEqual(
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 1)))
                .thenReturn(List.of(contract));

        var result = new SearchService(repository).search(new SearchRequest(
                LocalDate.of(2026, 6, 1), 1,
                List.of(new SearchRequest.RoomRequest(2), new SearchRequest.RoomRequest(2))));

        assertThat(result.getFirst().available()).isFalse();
    }
}
