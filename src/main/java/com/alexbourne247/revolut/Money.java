package com.alexbourne247.revolut;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

public class Money {

    private static final Currency GBP = Currency.getInstance("GBP");
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;

    private BigDecimal amount;
    private Currency currency;

    public static Money gbp(BigDecimal amount) {
        return new Money(amount, GBP);
    }

    Money(BigDecimal amount, Currency currency) {
        this(amount, currency, DEFAULT_ROUNDING);
    }

    Money(BigDecimal amount, Currency currency, RoundingMode rounding) {
        this.amount = amount;
        this.currency = currency;

        this.amount = amount.setScale(currency.getDefaultFractionDigits(), rounding);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return getCurrency().getSymbol() + " " + getAmount();
    }

    public String toString(Locale locale) {
        return getCurrency().getSymbol(locale) + " " + getAmount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;

        if (!amount.equals(money.amount)) return false;
        return currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        int result = amount.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }
}

