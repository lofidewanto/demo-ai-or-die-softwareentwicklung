package com.example.taa.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * The applicant / policyholder (Antragsteller bzw. Versicherungsnehmer:in).
 *
 * <p>Immutable value object bundling the personal, address and contact data
 * collected in the "Ihr Antrag" step of the process.
 */
public record Applicant(
		Salutation salutation,
		String firstName,
		String lastName,
		LocalDate birthDate,
		String street,
		String houseNumber,
		String postalCode,
		String city,
		String email,
		String phone) {

	public Applicant {
		Objects.requireNonNull(salutation, "salutation must not be null");
		Objects.requireNonNull(firstName, "firstName must not be null");
		Objects.requireNonNull(lastName, "lastName must not be null");
		Objects.requireNonNull(birthDate, "birthDate must not be null");
		Objects.requireNonNull(street, "street must not be null");
		Objects.requireNonNull(houseNumber, "houseNumber must not be null");
		Objects.requireNonNull(postalCode, "postalCode must not be null");
		Objects.requireNonNull(city, "city must not be null");
		Objects.requireNonNull(email, "email must not be null");
		// phone is optional and may be null
	}

	public String fullName() {
		return firstName + " " + lastName;
	}
}
