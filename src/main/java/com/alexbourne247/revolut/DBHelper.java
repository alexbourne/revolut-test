package com.alexbourne247.revolut;

import java.sql.*;

public class DBHelper {

    public static Connection getConnection(String user, String password) throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:mem:employees", user, password);
    }

    public static double getBalance(int accountId) throws SQLException {
        try (Connection connection = getConnection("alex", "alex"); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select balance from accounts where accountId = " + accountId);
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }

        return Double.NaN;
    }
}
