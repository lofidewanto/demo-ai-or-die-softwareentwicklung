package com.example.taa.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Immutable money value object.
 *
 * <p>Kept deliberately small and framework free so it can live in the domain
 * layer. Amounts are always stored with a scale of 2 (cents) and rounded
 * half-up, which is the expected behaviour for insurance premiums.
 */
public record Money(BigDecimal amount, String currency) {

	private static final Locale GERMANY = Locale.GERMANY;

	public Money {
		Objects.requireNonNull(amount, "amount must not be null");
		Objects.requireNonNull(currency, "currency must not be null");
		amount = amount.setScale(2, RoundingMode.HALF_UP);
	}

	/**
	 * Creates a euro amount from a plain value, e.g. {@code Money.euro("15.90")}.
	 */
	public static Money euro(String value) {
		return new Money(new BigDecimal(value), "EUR");
	}

	public static Money euro(BigDecimal value) {
		return new Money(value, "EUR");
	}

	public Money add(Money other) {
		requireSameCurrency(other);
		return new Money(this.amount.add(other.amount), this.currency);
	}

	private void requireSameCurrency(Money other) {
		if (!this.currency.equals(other.currency)) {
			throw new IllegalArgumentException(
					"Cannot combine amounts of different currencies: %s vs %s"
							.formatted(this.currency, other.currency));
		}
	}

	/**
	 * Locale-aware representation for the UI, e.g. {@code "15,90 €"}.
	 */
	public String format() {
		NumberFormat format = NumberFormat.getCurrencyInstance(GERMANY);
		format.setCurrency(Currency.getInstance(currency));
		return format.format(amount);
	}
}
