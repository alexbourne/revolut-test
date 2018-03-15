package com.alexbourne247.revolut;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.alexbourne247.revolut.DBHelper.getConnection;
import static com.alexbourne247.revolut.TransferStatus.*;
import static java.math.BigDecimal.ZERO;

public class BankSystem implements TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankSystem.class);
    private static final String TRANSFER_FAILED = "Funds transfer failed: ";


    /**
     * Transfer funds from one account to another
     *
     * @param fromAccountId The account to transfer funds from
     * @param toAccountId   The account to transfer funds to
     * @param amount of money to transfer
     * @return {@link TransferStatus} depending on outcome of transfer
     */
    @Override
    public TransferStatus transferFunds(int fromAccountId, int toAccountId, BigDecimal amount) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
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

            if (amount.compareTo(ZERO) < 1) {
                connection.rollback();
                LOGGER.info(TRANSFER_FAILED + INVALID_TRANSFER_AMOUNT);
                return INVALID_TRANSFER_AMOUNT;
            }

            BigDecimal fromBalance = getBalance(statement, fromAccountId);
            BigDecimal toBalance = getBalance(statement, toAccountId);

            if (amount.compareTo(fromBalance) >= 0) {
                connection.rollback();
                LOGGER.info(TRANSFER_FAILED + INSUFFICIENT_FUNDS);
                return INSUFFICIENT_FUNDS;
            }

            updateBalance(statement, fromAccountId, fromBalance.subtract(amount));
            updateBalance(statement, toAccountId, toBalance.add(amount));

            connection.commit();

        } catch (Exception e) {
            LOGGER.error("Funds transfer failed", e);
            return ERROR;
        }
        return TRANSFERRED;
    }

    private boolean accountExists(Statement statement, int accountId) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select 1 from accounts where accountId = " + accountId);
        return resultSet.next();
    }

    private BigDecimal getBalance(Statement statement, int accountId) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select balance from accounts where accountId = " + accountId);
        resultSet.next();
        BigDecimal balance = resultSet.getBigDecimal(1);
        return balance.setScale(2, RoundingMode.HALF_EVEN);
    }

    private void updateBalance(Statement statement, int accountId, BigDecimal amount) throws SQLException {
        statement.executeUpdate("update accounts set balance = " + amount +" where accountId = " + accountId);
    }

}
