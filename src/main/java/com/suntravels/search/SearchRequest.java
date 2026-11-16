package com.suntravels.search;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record SearchRequest(
        @NotNull LocalDate checkIn,
        @Min(1) int nights,
        @NotEmpty List<@Valid RoomRequest> rooms) {
    public record RoomRequest(@Min(1) int adults) {
    }
}
