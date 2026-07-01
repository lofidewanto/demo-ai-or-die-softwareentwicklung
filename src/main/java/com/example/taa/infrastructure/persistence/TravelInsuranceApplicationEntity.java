package com.example.taa.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import com.example.taa.domain.model.Salutation;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mapped to the {@code travel_insurance_application} table
 * (schema owned by Flyway, see {@code db/migration}).
 *
 * <p>Kept as a plain persistence adapter type; the mapping to/from the domain
 * aggregate is done by {@link TravelInsuranceApplicationMapper}. The pre-existing
 * NOT NULL columns from V1 ({@code destination}, {@code travel_start},
 * {@code travel_end}) are always populated by the mapper.
 */
@Entity
@Table(name = "travel_insurance_application")
public class TravelInsuranceApplicationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "salutation", nullable = false, length = 10)
	private Salutation salutation;

	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 100)
	private String lastName;

	@Column(name = "applicant_birth_date", nullable = false)
	private LocalDate applicantBirthDate;

	@Column(name = "email", nullable = false, length = 255)
	private String email;

	@Column(name = "phone", length = 50)
	private String phone;

	@Column(name = "street", nullable = false, length = 150)
	private String street;

	@Column(name = "house_number", nullable = false, length = 20)
	private String houseNumber;

	@Column(name = "postal_code", nullable = false, length = 10)
	private String postalCode;

	@Column(name = "city", nullable = false, length = 100)
	private String city;

	@Column(name = "iban", nullable = false, length = 34)
	private String iban;

	@Column(name = "destination", nullable = false, length = 100)
	private String destination;

	@Column(name = "travel_start", nullable = false)
	private LocalDate travelStart;

	@Column(name = "travel_end", nullable = false)
	private LocalDate travelEnd;

	@Column(name = "premium_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal premiumAmount;

	@Column(name = "premium_currency", nullable = false, length = 3)
	private String premiumCurrency;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = "travel_insured_person",
			joinColumns = @JoinColumn(name = "application_id", nullable = false))
	@OrderColumn(name = "person_index")
	private List<InsuredPersonEmbeddable> insuredPersons = new ArrayList<>();

	protected TravelInsuranceApplicationEntity() {
		// required by JPA
	}

	public Long getId() {
		return id;
	}

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

	public LocalDate getApplicantBirthDate() {
		return applicantBirthDate;
	}

	public void setApplicantBirthDate(LocalDate applicantBirthDate) {
		this.applicantBirthDate = applicantBirthDate;
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

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public LocalDate getTravelStart() {
		return travelStart;
	}

	public void setTravelStart(LocalDate travelStart) {
		this.travelStart = travelStart;
	}

	public LocalDate getTravelEnd() {
		return travelEnd;
	}

	public void setTravelEnd(LocalDate travelEnd) {
		this.travelEnd = travelEnd;
	}

	public BigDecimal getPremiumAmount() {
		return premiumAmount;
	}

	public void setPremiumAmount(BigDecimal premiumAmount) {
		this.premiumAmount = premiumAmount;
	}

	public String getPremiumCurrency() {
		return premiumCurrency;
	}

	public void setPremiumCurrency(String premiumCurrency) {
		this.premiumCurrency = premiumCurrency;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public List<InsuredPersonEmbeddable> getInsuredPersons() {
		return insuredPersons;
	}

	public void setInsuredPersons(List<InsuredPersonEmbeddable> insuredPersons) {
		this.insuredPersons = insuredPersons;
	}
}
