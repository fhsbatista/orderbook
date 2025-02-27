package com.fhsbatista.orderbook;

import java.util.UUID;

public record Order(
        UUID uuid,
        String assetCode,
        String type,
        double quantity,
        double price,
        String owner
) {
}
