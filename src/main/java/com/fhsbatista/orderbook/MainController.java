package com.fhsbatista.orderbook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

@RestController
public class MainController {
    private final UUIDGenerator uuidGenerator;

    MainController(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    @PostMapping("/orders")
    public ResponseEntity createOrder(CreateOrderInput input) {
        final String sql = "INSERT INTO orders (id, asset_code, type, quantity, price, owner) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            final String url = "jdbc:mysql://localhost:3306/orders";
            final String user = "root";
            final String password = "root";
            final Connection conn = DriverManager.getConnection(url, user, password);

            final PreparedStatement statement = conn.prepareStatement(sql);

            final UUID uuid = uuidGenerator.generate();
            statement.setString(1, uuid.toString());
            statement.setString(2, input.assetCode());
            statement.setString(3, input.type());
            statement.setDouble(4, input.quantity());
            statement.setDouble(5, input.price());
            statement.setString(6, input.owner());

            statement.executeUpdate();

            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/assets/{assetCode}/orders")
    public ResponseEntity<List<Order>> listOrders(@PathVariable String assetCode) {
        final String sql = "SELECT * FROM orders WHERE asset_code = ?";

        try {
            final String url = "jdbc:mysql://localhost:3306/orders";
            final String user = "root";
            final String password = "root";

            final Connection conn = DriverManager.getConnection(url, user, password);

            final PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, assetCode);

            final ResultSet result = statement.executeQuery();

            List<Order> orders = new ArrayList<>();

            while (result.next()) {
                orders.add(new Order(
                        UUID.fromString(result.getString("id")),
                        result.getString("asset_code"),
                        result.getString("type"),
                        result.getDouble("quantity"),
                        result.getDouble("price"),
                        result.getString("owner")
                ));
            }

            return ResponseEntity.ok().body(orders);

        } catch (SQLException e) {
        }

        return ResponseEntity.ok().build();
    }
}
