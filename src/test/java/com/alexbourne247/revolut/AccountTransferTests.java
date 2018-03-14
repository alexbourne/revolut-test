package com.alexbourne247.revolut;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ratpack.test.MainClassApplicationUnderTest;

import java.sql.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class AccountTransferTests {

    private MainClassApplicationUnderTest aut;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        initDatabase();
        aut = new MainClassApplicationUnderTest(TransferApp.class);
    }

    @After
    public void tearDown() throws Exception {
        aut.close();

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE accounts");
            connection.commit();
        }

    }

    @Test
    public void simpleGetReturnsMessage() {
        assertEquals("Account Transfer Service", get("/"));
    }

    @Test
    public void successfulTransferWithSufficientFunds() throws Exception {
        assertEquals(TransferStatus.TRANSFERRED, mapper.readValue(post("transfer", "{ \"fromAccountId\": 12345, \"toAccountId\": 23456, \"amount\": 150.99 }"), TransferStatus.class));
        assertEquals(getBalance(12345), 49.01, 0.01);
        assertEquals(getBalance(23456), 150.99, 0.01);    }

    @Test
    public void unsuccessfulTransferDueToInsufficientFunds() throws Exception {
        assertEquals(TransferStatus.INSUFFICIENT_FUNDS, mapper.readValue(post("transfer", "{ \"fromAccountId\": 12345, \"toAccountId\": 23456, \"amount\": 2000.00 }"), TransferStatus.class));
        assertEquals(getBalance(12345), 200.0, 0.01);
        assertEquals(getBalance(23456), 0.0, 0.01);
    }

    @Test
    public void fromAccountDoesntExist() throws Exception {
        assertEquals(TransferStatus.FROM_ACCOUNT_DOESNT_EXIST, mapper.readValue(post("transfer", "{ \"fromAccountId\": 99999, \"toAccountId\": 23456, \"amount\": 100.00 }"), TransferStatus.class));
    }

    @Test
    public void toAccountDoesntExist() throws Exception {
        assertEquals(TransferStatus.TO_ACCOUNT_DOESNT_EXIST, mapper.readValue(post("transfer", "{ \"fromAccountId\": 12345, \"toAccountId\": 99999, \"amount\": 100.00 }"), TransferStatus.class));
    }

    private String get(String path) {
        return aut.getHttpClient().getText(path);
    }

    private String post(String path, String body) {
        return aut.getHttpClient().request( path, req -> {
            req.method("POST");
            req.body(b -> b.type("application/json").text(body));
        }).getBody().getText();
    }


    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:mem:employees", "alex", "alex");
    }

    private static void initDatabase() throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE accounts (accountId INT NOT NULL, name VARCHAR(50) NOT NULL, balance FLOAT, PRIMARY KEY (accountId) )");
            connection.commit();

            statement.executeUpdate("INSERT INTO accounts VALUES (12345,'Big Dave', 200.0)");
            statement.executeUpdate("INSERT INTO accounts VALUES (23456,'Little Dave', 0.0)");
            connection.commit();
        }
    }

    private static double getBalance(int accountId) throws SQLException {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select balance from accounts where accountId = " + accountId);
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }

        return Double.NaN;
    }

}
