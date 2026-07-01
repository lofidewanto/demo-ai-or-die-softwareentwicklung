package com.example.taa.domain.service;

import com.example.taa.domain.model.InsuredPerson;
import com.example.taa.domain.model.Money;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Calculates the annual premium for the DEVK Auslandsreisekrankenversicherung
 * (worldwide cover, up to two months per trip).
 *
 * <p><b>Tariff model (demo).</b> Only the single-person / under-60 rate of
 * {@code 15,90 €} is a real, publicly documented DEVK figure. All other values
 * are transparent, self-consistent demo assumptions and do <em>not</em>
 * represent an actual DEVK tariff:
 *
 * <ul>
 *   <li>Person, age &lt; 60: 15,90 €</li>
 *   <li>Person, age &ge; 60 (senior): 42,00 €</li>
 *   <li>1–2 persons: sum of the individual rates</li>
 *   <li>3–4 persons (family flat rate), all &lt; 60: 39,90 €</li>
 *   <li>3–4 persons, at least one senior: 69,00 €</li>
 * </ul>
 *
 * <p>The age is evaluated at the insurance start date. This class is a pure
 * domain service (no framework dependencies) and is therefore trivially unit
 * testable.
 */
public class PremiumCalculator {

	/** Age threshold (inclusive) for the senior tariff band. */
	static final int SENIOR_AGE = 60;

	/** Maximum number of insured persons supported by the product. */
	public static final int MAX_INSURED_PERSONS = 4;

	static final Money RATE_ADULT = Money.euro("15.90");
	static final Money RATE_SENIOR = Money.euro("42.00");
	static final Money FAMILY_FLAT_RATE = Money.euro("39.90");
	static final Money FAMILY_FLAT_RATE_WITH_SENIOR = Money.euro("69.00");

	/** Number of persons from which the family flat rate applies. */
	static final int FAMILY_THRESHOLD = 3;

	/**
	 * Calculates the annual premium for the given insured persons.
	 *
	 * @param insuredPersons 1..{@value #MAX_INSURED_PERSONS} persons to insure
	 * @param insuranceStart the date the cover starts (used to determine ages)
	 * @return the annual premium in EUR
	 */
	public Money calculate(List<InsuredPerson> insuredPersons, LocalDate insuranceStart) {
		Objects.requireNonNull(insuredPersons, "insuredPersons must not be null");
		Objects.requireNonNull(insuranceStart, "insuranceStart must not be null");
		if (insuredPersons.isEmpty()) {
			throw new IllegalArgumentException("At least one insured person is required");
		}
		if (insuredPersons.size() > MAX_INSURED_PERSONS) {
			throw new IllegalArgumentException(
					"At most %d insured persons are supported".formatted(MAX_INSURED_PERSONS));
		}

		boolean anySenior = insuredPersons.stream()
				.anyMatch(person -> isSenior(person, insuranceStart));

		if (insuredPersons.size() >= FAMILY_THRESHOLD) {
			return anySenior ? FAMILY_FLAT_RATE_WITH_SENIOR : FAMILY_FLAT_RATE;
		}

		return insuredPersons.stream()
				.map(person -> rateFor(person, insuranceStart))
				.reduce(Money.euro("0.00"), Money::add);
	}

	private Money rateFor(InsuredPerson person, LocalDate insuranceStart) {
		return isSenior(person, insuranceStart) ? RATE_SENIOR : RATE_ADULT;
	}

	private boolean isSenior(InsuredPerson person, LocalDate insuranceStart) {
		return person.ageAt(insuranceStart) >= SENIOR_AGE;
	}
}
