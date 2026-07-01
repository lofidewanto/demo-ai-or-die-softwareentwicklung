package com.example.taa.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing a submitted application for the DEVK
 * Auslandsreisekrankenversicherung (annual, worldwide cover).
 *
 * <p>The {@code id} is {@code null} for applications that have not yet been
 * persisted. Use {@link #create} to build a new application; the persistence
 * layer assigns the technical id.
 */
public record TravelInsuranceApplication(
		Long id,
		LocalDate insuranceStart,
		List<InsuredPerson> insuredPersons,
		Applicant applicant,
		String iban,
		Money premium,
		Instant submittedAt) {

	public TravelInsuranceApplication {
		Objects.requireNonNull(insuranceStart, "insuranceStart must not be null");
		Objects.requireNonNull(insuredPersons, "insuredPersons must not be null");
		Objects.requireNonNull(applicant, "applicant must not be null");
		Objects.requireNonNull(iban, "iban must not be null");
		Objects.requireNonNull(premium, "premium must not be null");
		Objects.requireNonNull(submittedAt, "submittedAt must not be null");
		if (insuredPersons.isEmpty()) {
			throw new IllegalArgumentException("At least one insured person is required");
		}
		insuredPersons = List.copyOf(insuredPersons);
	}

	/**
	 * Factory for a new, not-yet-persisted application ({@code id == null}).
	 */
	public static TravelInsuranceApplication create(
			LocalDate insuranceStart,
			List<InsuredPerson> insuredPersons,
			Applicant applicant,
			String iban,
			Money premium,
			Instant submittedAt) {
		return new TravelInsuranceApplication(
				null, insuranceStart, insuredPersons, applicant, iban, premium, submittedAt);
	}

	public int numberOfInsuredPersons() {
		return insuredPersons.size();
	}

	/**
	 * End of the (annual) cover period, derived from the insurance start.
	 */
	public LocalDate insuranceEnd() {
		return insuranceStart.plusYears(1);
	}
}
