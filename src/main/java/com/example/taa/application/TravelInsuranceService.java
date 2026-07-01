package com.example.taa.application;

import com.example.taa.application.command.SubmitApplicationCommand;
import com.example.taa.domain.model.Applicant;
import com.example.taa.domain.model.InsuredPerson;
import com.example.taa.domain.model.Money;
import com.example.taa.domain.model.TravelInsuranceApplication;
import com.example.taa.domain.repository.TravelInsuranceApplicationRepository;
import com.example.taa.domain.service.PremiumCalculator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Application service orchestrating the travel insurance use cases:
 * calculating the premium ("Unser Angebot") and submitting an application
 * ("Ihr Antrag" / "Ihr Versicherungsschein").
 *
 * <p>It depends only on domain abstractions (the {@link PremiumCalculator}
 * domain service and the {@link TravelInsuranceApplicationRepository} port),
 * not on any persistence technology.
 */
@Service
public class TravelInsuranceService {

	private final PremiumCalculator premiumCalculator;
	private final TravelInsuranceApplicationRepository repository;
	private final Clock clock;

	public TravelInsuranceService(
			PremiumCalculator premiumCalculator,
			TravelInsuranceApplicationRepository repository,
			Clock clock) {
		this.premiumCalculator = premiumCalculator;
		this.repository = repository;
		this.clock = clock;
	}

	/**
	 * Calculates the annual premium for the given insured persons and start date
	 * without persisting anything (used for the "Unser Angebot" step).
	 */
	public Money calculatePremium(List<LocalDate> insuredBirthDates, LocalDate insuranceStart) {
		return premiumCalculator.calculate(toInsuredPersons(insuredBirthDates), insuranceStart);
	}

	/**
	 * Assembles the domain aggregate, (re-)calculates the premium authoritatively
	 * and persists the application.
	 *
	 * @return the persisted application including its assigned id
	 */
	@Transactional
	public TravelInsuranceApplication submit(SubmitApplicationCommand command) {
		List<InsuredPerson> insuredPersons = toInsuredPersons(command.insuredBirthDates());
		Money premium = premiumCalculator.calculate(insuredPersons, command.insuranceStart());

		Applicant applicant = new Applicant(
				command.salutation(),
				command.firstName().strip(),
				command.lastName().strip(),
				command.applicantBirthDate(),
				command.street().strip(),
				command.houseNumber().strip(),
				command.postalCode().strip(),
				command.city().strip(),
				command.email().strip(),
				normalizePhone(command.phone()));

		TravelInsuranceApplication application = TravelInsuranceApplication.create(
				command.insuranceStart(),
				insuredPersons,
				applicant,
				normalizeIban(command.iban()),
				premium,
				clock.instant());

		return repository.save(application);
	}

	private static List<InsuredPerson> toInsuredPersons(List<LocalDate> birthDates) {
		return birthDates.stream().map(InsuredPerson::new).toList();
	}

	private static String normalizeIban(String iban) {
		return iban.replaceAll("\\s", "").toUpperCase();
	}

	private static String normalizePhone(String phone) {
		if (phone == null || phone.isBlank()) {
			return null;
		}
		return phone.strip();
	}
}
