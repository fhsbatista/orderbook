package com.fhsbatista.orderbook;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.*;

@RestController
public class MainController {
    private static final String URL = "jdbc:mysql://localhost:3306/orders";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private final UUIDGenerator uuidGenerator;

    MainController(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @PostMapping("/orders")
    public ResponseEntity createOrder(CreateOrderInput input) {
        if (input.type().equals("sell")) {
            final String sql = "INSERT INTO orders (id, asset_code, type, quantity, price, owner) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = getConnection().prepareStatement(sql)) {

                final UUID uuid = uuidGenerator.generate();
                statement.setString(1, uuid.toString());
                statement.setString(2, input.assetCode());
                statement.setString(3, input.type());
                statement.setDouble(4, input.quantity());
                statement.setDouble(5, input.price());
                statement.setString(6, input.owner());

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (input.type().equals("buy")) {
            final String sql = "SELECT * FROM orders WHERE asset_code = ? AND type = ?";

            try (Connection conn = getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, input.assetCode());
                statement.setString(2, "sell");

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        if (result.getDouble("price") == input.price()) {
                            final String deleteSql = "DELETE FROM orders WHERE id = ?";
                            try (PreparedStatement deleteStatement = getConnection().prepareStatement(deleteSql)) {
                                deleteStatement.setString(1, result.getString("id"));
                                deleteStatement.executeUpdate();
                            }

                            return ResponseEntity.ok().build();
                        }
                    }

                }

                final String insertSql = "INSERT INTO orders (id, asset_code, type, quantity, price, owner) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement insertStatement = getConnection().prepareStatement(insertSql)) {
                    final UUID uuid = uuidGenerator.generate();
                    insertStatement.setString(1, uuid.toString());
                    insertStatement.setString(2, input.assetCode());
                    insertStatement.setString(3, input.type());
                    insertStatement.setDouble(4, input.quantity());
                    insertStatement.setDouble(5, input.price());
                    insertStatement.setString(6, input.owner());

                    insertStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok().build();
    }


    @GetMapping("/assets/{assetCode}/orders")
    public ResponseEntity<List<Order>> listOrders(@PathVariable String assetCode) {
        final String sql = "SELECT * FROM orders WHERE asset_code = ?";

        try {
            final ResultSet result;
            try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
                statement.setString(1, assetCode);

                result = statement.executeQuery();

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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }
}
