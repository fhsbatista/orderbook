package com.fhsbatista.orderbook;

public record CreateOrderInput(
        String assetCode,
        String type,
        double quantity,
        double price,
        String owner
) {
}
