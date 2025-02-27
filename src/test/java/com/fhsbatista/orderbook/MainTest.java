package com.fhsbatista.orderbook;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MainTest {
    private UUIDGenerator uuidGenerator;

    @InjectMocks
    private MainController controller;

    @BeforeEach
    public void setup() {
        uuidGenerator = mock(UUIDGenerator.class);
        controller = new MainController(uuidGenerator);
    }

    @BeforeEach
    public void cleanDatabase() {
        DatabaseCleaner.clean("orders");
    }

    @Test
    public void mustCreateAssetSellOrders() {
        final var uuid = UUID.randomUUID();
        when(uuidGenerator.generate()).thenReturn(uuid);
        var input = new CreateOrderInput(
                "USDC",
                "sell",
                1000,
                5.50,
                 "John Doe"
        );
        var createResult = controller.createOrder(input);

        assertEquals(200, createResult.getStatusCode().value());

        var listResult = controller.listOrders(input.assetCode());

        assertEquals(200, listResult.getStatusCode().value());

        var expectedOrder = new Order(
                uuid,
                input.assetCode(),
                input.type(),
                input.quantity(),
                input.price(),
                input.owner()
        );

        assertEquals(List.of(expectedOrder), listResult.getBody());
    }
}
