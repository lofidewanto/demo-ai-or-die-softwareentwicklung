package com.example.taa.domain.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * A person to be insured under the travel health insurance policy.
 *
 * <p>For the DEVK Auslandsreisekrankenversicherung the only rating-relevant
 * attribute of an insured person is the date of birth (the age at insurance
 * start determines the tariff band).
 */
public record InsuredPerson(LocalDate birthDate) {

	public InsuredPerson {
		Objects.requireNonNull(birthDate, "birthDate must not be null");
	}

	/**
	 * Completed years of age at the given reference date (e.g. the insurance
	 * start date).
	 */
	public int ageAt(LocalDate reference) {
		Objects.requireNonNull(reference, "reference must not be null");
		return Period.between(birthDate, reference).getYears();
	}
}
