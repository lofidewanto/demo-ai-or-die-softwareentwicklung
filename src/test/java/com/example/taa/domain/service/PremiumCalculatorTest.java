package com.example.taa.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.example.taa.domain.model.InsuredPerson;
import com.example.taa.domain.model.Money;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class PremiumCalculatorTest {

	private static final LocalDate START = LocalDate.of(2026, 1, 1);

	private final PremiumCalculator calculator = new PremiumCalculator();

	private static InsuredPerson aged(int years) {
		return new InsuredPerson(START.minusYears(years).minusDays(1));
	}

	@Test
	@DisplayName("single person under 60 pays the documented 15,90 €")
	void singleAdult() {
		Money premium = calculator.calculate(List.of(aged(30)), START);
		assertThat(premium).isEqualTo(Money.euro("15.90"));
	}

	@Test
	@DisplayName("single senior (>= 60) pays the senior rate")
	void singleSenior() {
		Money premium = calculator.calculate(List.of(aged(60)), START);
		assertThat(premium).isEqualTo(Money.euro("42.00"));
	}

	@Test
	@DisplayName("two adults are the sum of the individual rates")
	void twoAdults() {
		Money premium = calculator.calculate(List.of(aged(30), aged(40)), START);
		assertThat(premium).isEqualTo(Money.euro("31.80"));
	}

	@Test
	@DisplayName("two persons with one senior sum adult + senior rate")
	void twoPersonsOneSenior() {
		Money premium = calculator.calculate(List.of(aged(30), aged(65)), START);
		assertThat(premium).isEqualTo(Money.euro("57.90"));
	}

	@Test
	@DisplayName("family of three (all < 60) pays the family flat rate")
	void familyThreeAdults() {
		Money premium = calculator.calculate(List.of(aged(35), aged(33), aged(5)), START);
		assertThat(premium).isEqualTo(Money.euro("39.90"));
	}

	@Test
	@DisplayName("family of four with a senior pays the senior family flat rate")
	void familyFourWithSenior() {
		Money premium = calculator.calculate(List.of(aged(35), aged(60), aged(8), aged(4)), START);
		assertThat(premium).isEqualTo(Money.euro("69.00"));
	}

	@Test
	@DisplayName("age is evaluated at the insurance start date (turns 60 on start day)")
	void ageBoundaryAtStart() {
		InsuredPerson turns60OnStart = new InsuredPerson(START.minusYears(60));
		Money premium = calculator.calculate(List.of(turns60OnStart), START);
		assertThat(premium).isEqualTo(Money.euro("42.00"));
	}

	@Test
	@DisplayName("rejects an empty list of persons")
	void rejectsEmpty() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> calculator.calculate(List.of(), START));
	}

	@Test
	@DisplayName("rejects more than four persons")
	void rejectsTooMany() {
		List<InsuredPerson> five = List.of(aged(1), aged(2), aged(3), aged(4), aged(5));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> calculator.calculate(five, START));
	}
}
