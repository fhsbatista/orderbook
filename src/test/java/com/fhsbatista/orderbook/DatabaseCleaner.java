package com.fhsbatista.orderbook;

import java.sql.*;

public class DatabaseCleaner {
    private static final String URL = "jdbc:mysql://localhost:3306/orders";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static void clean(String table) {
        try {
            final String sql = "DELETE FROM " + table;
            final Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            final PreparedStatement statement = conn.prepareStatement(sql);

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
