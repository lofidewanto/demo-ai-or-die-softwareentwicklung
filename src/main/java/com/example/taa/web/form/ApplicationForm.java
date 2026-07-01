package com.example.taa.web.form;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.taa.domain.model.Salutation;
import com.example.taa.web.validation.Iban;

import java.time.LocalDate;

/**
 * Form backing object for the "Ihr Antrag" step: applicant / policyholder data,
 * bank details (IBAN) and the required consents.
 */
public class ApplicationForm {

	@NotNull(message = "{ark.salutation.required}")
	private Salutation salutation;

	@NotBlank(message = "{ark.firstName.required}")
	@Size(max = 100, message = "{ark.field.tooLong}")
	private String firstName;

	@NotBlank(message = "{ark.lastName.required}")
	@Size(max = 100, message = "{ark.field.tooLong}")
	private String lastName;

	@NotNull(message = "{ark.applicantBirthDate.required}")
	@Past(message = "{ark.birthDate.past}")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate birthDate;

	@NotBlank(message = "{ark.street.required}")
	@Size(max = 150, message = "{ark.field.tooLong}")
	private String street;

	@NotBlank(message = "{ark.houseNumber.required}")
	@Size(max = 20, message = "{ark.field.tooLong}")
	private String houseNumber;

	@NotBlank(message = "{ark.postalCode.required}")
	@Pattern(regexp = "\\d{5}", message = "{ark.postalCode.invalid}")
	private String postalCode;

	@NotBlank(message = "{ark.city.required}")
	@Size(max = 100, message = "{ark.field.tooLong}")
	private String city;

	@NotBlank(message = "{ark.email.required}")
	@Email(message = "{ark.email.invalid}")
	@Size(max = 255, message = "{ark.field.tooLong}")
	private String email;

	@Pattern(regexp = "^[+0-9 ()/.\\-]{0,40}$", message = "{ark.phone.invalid}")
	private String phone;

	@NotBlank(message = "{ark.iban.required}")
	@Iban
	private String iban;

	@AssertTrue(message = "{ark.acceptTerms.required}")
	private boolean acceptTerms;

	@AssertTrue(message = "{ark.acceptPrivacy.required}")
	private boolean acceptPrivacy;

	public Salutation getSalutation() {
		return salutation;
	}

	public void setSalutation(Salutation salutation) {
		this.salutation = salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public boolean isAcceptTerms() {
		return acceptTerms;
	}

	public void setAcceptTerms(boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}

	public boolean isAcceptPrivacy() {
		return acceptPrivacy;
	}

	public void setAcceptPrivacy(boolean acceptPrivacy) {
		this.acceptPrivacy = acceptPrivacy;
	}
}
