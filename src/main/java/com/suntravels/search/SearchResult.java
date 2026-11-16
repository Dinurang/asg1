package com.suntravels.search;

import java.math.BigDecimal;

public record SearchResult(
        Long contractId,
        String hotelName,
        String roomType,
        BigDecimal price,
        boolean available,
        int roomsRequested,
        int adults) {
}
