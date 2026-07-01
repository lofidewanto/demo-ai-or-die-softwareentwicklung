package com.example.taa.web.view;

import com.example.taa.domain.model.InsuredPerson;
import com.example.taa.domain.model.TravelInsuranceApplication;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Presentation model for the confirmation page ("Ihr Versicherungsschein").
 *
 * <p>Exposes plain getters and pre-formatted, display-ready values so the
 * Thymeleaf template stays simple and does not depend on the domain aggregate's
 * record accessors. This keeps the view layer decoupled from the domain model.
 */
public class ConfirmationView {

	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private final String applicationNumber;
	private final String salutationLabel;
	private final String fullName;
	private final String street;
	private final String houseNumber;
	private final String postalCode;
	private final String city;
	private final String email;
	private final String insuranceStart;
	private final List<String> insuredBirthDates;
	private final String premium;

	public ConfirmationView(TravelInsuranceApplication application, String premium) {
		this.applicationNumber = "ARK-%06d".formatted(application.id());
		this.salutationLabel = application.applicant().salutation().getLabel();
		this.fullName = application.applicant().fullName();
		this.street = application.applicant().street();
		this.houseNumber = application.applicant().houseNumber();
		this.postalCode = application.applicant().postalCode();
		this.city = application.applicant().city();
		this.email = application.applicant().email();
		this.insuranceStart = application.insuranceStart().format(DATE);
		this.insuredBirthDates = application.insuredPersons().stream()
				.map(InsuredPerson::birthDate)
				.map(DATE::format)
				.toList();
		this.premium = premium;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public String getSalutationLabel() {
		return salutationLabel;
	}

	public String getFullName() {
		return fullName;
	}

	public String getStreet() {
		return street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCity() {
		return city;
	}

	public String getEmail() {
		return email;
	}

	public String getInsuranceStart() {
		return insuranceStart;
	}

	public List<String> getInsuredBirthDates() {
		return insuredBirthDates;
	}

	public String getPremium() {
		return premium;
	}
}
