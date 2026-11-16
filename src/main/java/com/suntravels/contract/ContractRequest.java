package com.suntravels.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ContractRequest(
        @NotBlank String hotelName,
        @NotNull LocalDate validFrom,
        @NotNull LocalDate validTo,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal markupPercent,
        @NotEmpty List<@Valid RoomTypeRequest> roomTypes) {
    public record RoomTypeRequest(
            @NotBlank String name,
            @DecimalMin("0.01") BigDecimal pricePerPersonPerNight,
            @jakarta.validation.constraints.Min(0) int rooms,
            @jakarta.validation.constraints.Min(1) int maxAdults) {
    }
}
