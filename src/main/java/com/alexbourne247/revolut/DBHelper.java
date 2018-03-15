package com.alexbourne247.revolut;

import java.sql.*;

import static com.alexbourne247.revolut.Money.GBP;

public class DBHelper {
    public static final String USER = "alex";
    public static final String PASSWORD = "alex";

    /*
        A helper class to make testing less messy - obviously in a real system users/passwords/connection strings would be externalised

        Also, for brevity I've used plain strings for SQL statements - in a production system I would most likely use FlyWay and Jooq, but that
        would take too long to set up for a simple app like this.
     */

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:mem:employees", USER, PASSWORD);
    }

    public static Money getBalance(int accountId) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select balance from accounts where accountId = " + accountId);
            if (rs.next()) {
                return new Money(rs.getBigDecimal(1), GBP);
            }
        }

        return null;
    }

    public static void initDatabase() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE accounts (accountId INT NOT NULL, name VARCHAR(50) NOT NULL, balance DECIMAL(100,10), PRIMARY KEY (accountId) )");
            connection.commit();

            statement.executeUpdate("INSERT INTO accounts VALUES (12345,'Big Dave', 200.0)");
            statement.executeUpdate("INSERT INTO accounts VALUES (23456,'Little Dave', 0.0)");
            connection.commit();
        }
    }

    public static void killDatabase() {
        try (Connection connection = getConnection()) {
            connection.createStatement().execute("SHUTDOWN");
        } catch (Exception e) {
            // do nothing as this is expected
        }
    }

}
