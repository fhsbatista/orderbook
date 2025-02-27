package com.fhsbatista.orderbook;

import com.sun.tools.javac.Main;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void mustCreateAssetSellOrder() {
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

    @Test
    public void mustCreateAssetBuyOrder() {
        final var uuid = UUID.randomUUID();
        when(uuidGenerator.generate()).thenReturn(uuid);
        final var input = new CreateOrderInput(
                "USDC",
                "buy",
                30,
                20.3,
                "John Doe"
        );

        var createResult = controller.createOrder(input);

        assertEquals(200, createResult.getStatusCode().value());

        var listResult = controller.listOrders(input.assetCode());

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

    @Test
    public void skipBuyOrderIfExistsSellOrderWhichPriceIsEqual() {
        controller = new MainController(new UUIDGeneratorUtilAdapter());
        final var sellOrderInput = new CreateOrderInput(
                "USDC",
                "sell",
                30,
                20,
                "John Doe"
        );
        final var buyOrderInput = new CreateOrderInput(
                "USDC",
                "buy",
                30,
                20,
                "Jack Doe"
        );
        controller.createOrder(sellOrderInput);
        controller.createOrder(buyOrderInput);

        final var listResult = controller.listOrders("USDC");

        assertNotNull(listResult.getBody());
        assertEquals(0, listResult.getBody().size());
    }
}
