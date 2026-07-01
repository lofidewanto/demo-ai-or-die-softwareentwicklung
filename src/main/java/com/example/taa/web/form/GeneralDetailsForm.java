package com.example.taa.web.form;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Form backing object for the "Allgemeine Angaben" step: desired insurance
 * start, number of persons (1..4) and each person's date of birth.
 *
 * <p>Field-level constraints cover format, "must be in the past" and range.
 * The <em>conditional</em> requiredness of persons 2..4 (depending on
 * {@link #numberOfPersons}) is enforced in the controller so the error can be
 * attached to the exact birth-date field.
 */
public class GeneralDetailsForm {

	@NotNull(message = "{ark.insuranceStart.required}")
	@FutureOrPresent(message = "{ark.insuranceStart.futureOrPresent}")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate insuranceStart;

	@NotNull(message = "{ark.numberOfPersons.required}")
	@Min(value = 1, message = "{ark.numberOfPersons.range}")
	@Max(value = 4, message = "{ark.numberOfPersons.range}")
	private Integer numberOfPersons = 1;

	@Past(message = "{ark.birthDate.past}")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate birthDate1;

	@Past(message = "{ark.birthDate.past}")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate birthDate2;

	@Past(message = "{ark.birthDate.past}")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate birthDate3;

	@Past(message = "{ark.birthDate.past}")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate birthDate4;

	/**
	 * The birth dates of exactly {@link #numberOfPersons} persons, in order.
	 * Assumes the form has already been validated.
	 */
	public List<LocalDate> selectedBirthDates() {
		List<LocalDate> all = new ArrayList<>();
		all.add(birthDate1);
		all.add(birthDate2);
		all.add(birthDate3);
		all.add(birthDate4);
		int count = numberOfPersons == null ? 0 : numberOfPersons;
		return all.subList(0, Math.min(count, all.size()));
	}

	public LocalDate birthDateForPerson(int index) {
		return switch (index) {
			case 1 -> birthDate1;
			case 2 -> birthDate2;
			case 3 -> birthDate3;
			case 4 -> birthDate4;
			default -> null;
		};
	}

	public LocalDate getInsuranceStart() {
		return insuranceStart;
	}

	public void setInsuranceStart(LocalDate insuranceStart) {
		this.insuranceStart = insuranceStart;
	}

	public Integer getNumberOfPersons() {
		return numberOfPersons;
	}

	public void setNumberOfPersons(Integer numberOfPersons) {
		this.numberOfPersons = numberOfPersons;
	}

	public LocalDate getBirthDate1() {
		return birthDate1;
	}

	public void setBirthDate1(LocalDate birthDate1) {
		this.birthDate1 = birthDate1;
	}

	public LocalDate getBirthDate2() {
		return birthDate2;
	}

	public void setBirthDate2(LocalDate birthDate2) {
		this.birthDate2 = birthDate2;
	}

	public LocalDate getBirthDate3() {
		return birthDate3;
	}

	public void setBirthDate3(LocalDate birthDate3) {
		this.birthDate3 = birthDate3;
	}

	public LocalDate getBirthDate4() {
		return birthDate4;
	}

	public void setBirthDate4(LocalDate birthDate4) {
		this.birthDate4 = birthDate4;
	}
}
