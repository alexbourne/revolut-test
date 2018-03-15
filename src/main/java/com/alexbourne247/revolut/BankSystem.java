package com.alexbourne247.revolut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.alexbourne247.revolut.DBHelper.getConnection;
import static com.alexbourne247.revolut.TransferStatus.*;

public class BankSystem implements TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankSystem.class);
    private static final String TRANSFER_FAILED = "Funds transfer failed: ";


    @Override
    public TransferStatus transferFunds(int fromAccountId, int toAccountId, double amount) {
        try (Connection connection = getConnection("alex", "alex"); Statement statement = connection.createStatement()) {
            statement.execute("START TRANSACTION");

            if (!accountExists(statement, fromAccountId)) {
                connection.rollback();
                LOGGER.info(TRANSFER_FAILED + FROM_ACCOUNT_DOESNT_EXIST);
                return FROM_ACCOUNT_DOESNT_EXIST;
            }

            if (!accountExists(statement, toAccountId)) {
                connection.rollback();
                LOGGER.info(TRANSFER_FAILED + TO_ACCOUNT_DOESNT_EXIST);
                return TO_ACCOUNT_DOESNT_EXIST;
            }

            if (amount < 0.0) {
                connection.rollback();
                LOGGER.info(TRANSFER_FAILED + INVALID_TRANSFER_AMOUNT);
                return INVALID_TRANSFER_AMOUNT;
            }

            double fromBalance = getBalance(statement, fromAccountId);
            double toBalance = getBalance(statement, toAccountId);

            if (amount > fromBalance) {
                connection.rollback();
                LOGGER.info(TRANSFER_FAILED + INSUFFICIENT_FUNDS);
                return INSUFFICIENT_FUNDS;
            }

            updateBalance(statement, fromAccountId, fromBalance - amount);
            updateBalance(statement, toAccountId, toBalance + amount);

            connection.commit();

        } catch (SQLException e) {
            LOGGER.error("Funds transfer failed", e);
            return ERROR;
        }
        return TRANSFERRED;
    }

    private boolean accountExists(Statement statement, int accountId) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select 1 from accounts where accountId = " + accountId);
        return resultSet.next();
    }

    private double getBalance(Statement statement, int accountId) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select balance from accounts where accountId = " + accountId);
        resultSet.next();
        return resultSet.getDouble(1);
    }

    private void updateBalance(Statement statement, int accountId, double amount) throws SQLException {
        statement.executeUpdate("update accounts set balance = " + amount +" where accountId = " + accountId);
    }

}
