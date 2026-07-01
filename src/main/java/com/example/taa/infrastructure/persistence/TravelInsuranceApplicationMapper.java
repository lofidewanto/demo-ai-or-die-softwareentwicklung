package com.example.taa.infrastructure.persistence;

import com.example.taa.domain.model.Applicant;
import com.example.taa.domain.model.InsuredPerson;
import com.example.taa.domain.model.Money;
import com.example.taa.domain.model.TravelInsuranceApplication;

import java.util.List;

/**
 * Maps between the domain aggregate {@link TravelInsuranceApplication} and the
 * JPA entity {@link TravelInsuranceApplicationEntity}.
 *
 * <p>Also fills the pre-existing V1 columns that have no direct domain
 * counterpart: {@code destination} is fixed to "Weltweit" (the product provides
 * worldwide cover) and the travel period is derived from the annual cover.
 */
final class TravelInsuranceApplicationMapper {

	/** The DEVK Auslandsreisekrankenversicherung always provides worldwide cover. */
	private static final String WORLDWIDE = "Weltweit";

	private TravelInsuranceApplicationMapper() {
	}

	static TravelInsuranceApplicationEntity toEntity(TravelInsuranceApplication application) {
		TravelInsuranceApplicationEntity entity = new TravelInsuranceApplicationEntity();

		Applicant applicant = application.applicant();
		entity.setSalutation(applicant.salutation());
		entity.setFirstName(applicant.firstName());
		entity.setLastName(applicant.lastName());
		entity.setApplicantBirthDate(applicant.birthDate());
		entity.setEmail(applicant.email());
		entity.setPhone(applicant.phone());
		entity.setStreet(applicant.street());
		entity.setHouseNumber(applicant.houseNumber());
		entity.setPostalCode(applicant.postalCode());
		entity.setCity(applicant.city());

		entity.setIban(application.iban());
		entity.setDestination(WORLDWIDE);
		entity.setTravelStart(application.insuranceStart());
		entity.setTravelEnd(application.insuranceEnd());
		entity.setPremiumAmount(application.premium().amount());
		entity.setPremiumCurrency(application.premium().currency());
		entity.setCreatedAt(application.submittedAt());

		entity.setInsuredPersons(application.insuredPersons().stream()
				.map(person -> new InsuredPersonEmbeddable(person.birthDate()))
				.toList());

		return entity;
	}

	static TravelInsuranceApplication toDomain(TravelInsuranceApplicationEntity entity) {
		Applicant applicant = new Applicant(
				entity.getSalutation(),
				entity.getFirstName(),
				entity.getLastName(),
				entity.getApplicantBirthDate(),
				entity.getStreet(),
				entity.getHouseNumber(),
				entity.getPostalCode(),
				entity.getCity(),
				entity.getEmail(),
				entity.getPhone());

		List<InsuredPerson> insuredPersons = entity.getInsuredPersons().stream()
				.map(embeddable -> new InsuredPerson(embeddable.getBirthDate()))
				.toList();

		Money premium = new Money(entity.getPremiumAmount(), entity.getPremiumCurrency());

		return new TravelInsuranceApplication(
				entity.getId(),
				entity.getTravelStart(),
				insuredPersons,
				applicant,
				entity.getIban(),
				premium,
				entity.getCreatedAt());
	}
}
