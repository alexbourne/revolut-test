package com.alexbourne247.revolut;

import java.sql.*;

import static com.alexbourne247.revolut.TransferStatus.*;
import static com.alexbourne247.revolut.TransferStatus.FROM_ACCOUNT_DOESNT_EXIST;

public class BankSystem implements TransferService {

    @Override
    public TransferStatus transferFunds(int fromAccountId, int toAccountId, double amount) {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("START TRANSACTION");
            if (!accountExists(statement, fromAccountId)) {
                connection.rollback();
                return FROM_ACCOUNT_DOESNT_EXIST;
            }
            if (!accountExists(statement, toAccountId)) {
                connection.rollback();
                return TO_ACCOUNT_DOESNT_EXIST;
            }
            double fromBalance = getBalance(statement, fromAccountId);
            double toBalance = getBalance(statement, toAccountId);

            if (amount > fromBalance) {
                connection.rollback();
                return TransferStatus.INSUFFICIENT_FUNDS;
            }
            statement.executeUpdate("update accounts set balance = " + (fromBalance - amount) +" where accountId = " + fromAccountId);
            statement.executeUpdate("update accounts set balance = " + (toBalance + amount) +" where accountId = " + toAccountId);
            connection.commit();
        } catch (SQLException e) {
            // LOG ERROR
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

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:mem:employees", "alex", "alex");
    }

}
