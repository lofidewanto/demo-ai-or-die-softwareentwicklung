package com.example.taa.application.command;

import com.example.taa.domain.model.Salutation;

import java.time.LocalDate;
import java.util.List;

/**
 * Input DTO for submitting a travel insurance application.
 *
 * <p>Carries plain, already-validated values from the web layer into the
 * application service, which assembles the domain aggregate. Keeping this as a
 * dedicated command object decouples the use case from the web forms.
 */
public record SubmitApplicationCommand(
		LocalDate insuranceStart,
		List<LocalDate> insuredBirthDates,
		Salutation salutation,
		String firstName,
		String lastName,
		LocalDate applicantBirthDate,
		String street,
		String houseNumber,
		String postalCode,
		String city,
		String email,
		String phone,
		String iban) {
}
