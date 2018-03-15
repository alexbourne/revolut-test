package com.alexbourne247.revolut;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ratpack.test.MainClassApplicationUnderTest;

import java.sql.Connection;
import java.sql.Statement;

import static com.alexbourne247.revolut.DBHelper.getBalance;
import static com.alexbourne247.revolut.DBHelper.getConnection;
import static com.alexbourne247.revolut.Money.gbp;
import static com.alexbourne247.revolut.TransferStatus.*;
import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AccountTransferTests {

    private MainClassApplicationUnderTest aut;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        aut = new MainClassApplicationUnderTest(TransferApp.class);
    }

    @After
    public void tearDown() {
        aut.close();

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE ACCOUNTS");
            connection.commit();
        } catch (Exception e) {
            // ignore this - for some cases the table will not exist
        }

    }

    @Test
    public void simpleGetReturnsMessage() {
        assertEquals("Account Transfer Service", get("/"));
    }

    @Test
    public void successfulTransferWithSufficientFunds() throws Exception {
        assertEquals(TRANSFERRED, mapper.readValue(post("transfer", "{ \"fromAccountId\": 12345, \"toAccountId\": 23456, \"amount\": 150.99 }"), TransferStatus.class));
        assertEquals(gbp(49.01), getBalance(12345));
        assertEquals(gbp(150.99), getBalance(23456));    }

    @Test
    public void unsuccessfulTransferDueToInsufficientFunds() throws Exception {
        assertEquals(INSUFFICIENT_FUNDS, mapper.readValue(post("transfer", "{ \"fromAccountId\": 12345, \"toAccountId\": 23456, \"amount\": 2000.00 }"), TransferStatus.class));
        assertEquals(gbp(200.00), getBalance(12345));
        assertEquals(gbp(0.0), getBalance(23456));
    }

    @Test
    public void fromAccountDoesntExist() throws Exception {
        assertEquals(FROM_ACCOUNT_DOESNT_EXIST, mapper.readValue(post("transfer", "{ \"fromAccountId\": 99999, \"toAccountId\": 23456, \"amount\": 100.00 }"), TransferStatus.class));
    }

    @Test
    public void toAccountDoesntExist() throws Exception {
        assertEquals(TO_ACCOUNT_DOESNT_EXIST, mapper.readValue(post("transfer", "{ \"fromAccountId\": 12345, \"toAccountId\": 99999, \"amount\": 100.00 }"), TransferStatus.class));
    }

    @Test
    public void negativeAmount() throws Exception {
        assertEquals(INVALID_TRANSFER_AMOUNT, mapper.readValue(post("transfer", "{ \"fromAccountId\": 12345, \"toAccountId\": 23456, \"amount\": -100.00 }"), TransferStatus.class));
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

}
