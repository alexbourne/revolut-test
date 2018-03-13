package com.alexbourne247.revolut;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ratpack.test.MainClassApplicationUnderTest;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AccountTransferTests {

    private MainClassApplicationUnderTest aut = new MainClassApplicationUnderTest(TransferApp.class);
    private ObjectMapper mapper = new ObjectMapper();

    @After
    public void tearDown() {
        aut.close();
    }

    @Test
    public void simpleGetReturnsMessage() {
        assertEquals("Account Transfer Service", get("/"));
    }

    @Test
    public void successfulTransferWithSufficientFunds() throws Exception {
        assertEquals(TransferStatus.TRANSFERRED, mapper.readValue(post("transfer", ""), TransferStatus.class));
    }

    private String get(String path) {
        return aut.getHttpClient().getText(path);
    }

    private String post(String path, String body) {
        return aut.getHttpClient().request( path, req -> {
            req.method("POST");
            req.getBody().text(body);
        }).getBody().getText();
    }
}
