package com.example.taa.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.taa.domain.model.Applicant;
import com.example.taa.domain.model.InsuredPerson;
import com.example.taa.domain.model.Money;
import com.example.taa.domain.model.Salutation;
import com.example.taa.domain.model.TravelInsuranceApplication;
import com.example.taa.domain.repository.TravelInsuranceApplicationRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Verifies the persistence adapter maps the domain aggregate to/from the JPA
 * entity and that the Flyway-managed schema (incl. the insured-person child
 * table) works end to end against H2.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=none")
@Import(TravelInsuranceApplicationRepositoryAdapter.class)
class TravelInsuranceApplicationRepositoryAdapterTest {

	@Autowired
	private TravelInsuranceApplicationRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void savesAndReloadsApplicationIncludingInsuredPersons() {
		TravelInsuranceApplication toSave = TravelInsuranceApplication.create(
				LocalDate.of(2026, 8, 1),
				List.of(
						new InsuredPerson(LocalDate.of(1990, 5, 15)),
						new InsuredPerson(LocalDate.of(2015, 9, 20))),
				new Applicant(
						Salutation.FRAU, "Erika", "Mustermann", LocalDate.of(1990, 5, 15),
						"Musterstraße", "1", "50735", "Köln",
						"erika@example.com", "0221 123456"),
				"DE89370400440532013000",
				Money.euro("31.80"),
				Instant.parse("2026-07-01T10:15:30Z"));

		TravelInsuranceApplication saved = repository.save(toSave);
		assertThat(saved.id()).isNotNull();

		// Force a real round-trip through the database.
		entityManager.flush();
		entityManager.clear();

		Optional<TravelInsuranceApplication> reloaded = repository.findById(saved.id());
		assertThat(reloaded).isPresent();
		TravelInsuranceApplication application = reloaded.get();

		assertThat(application.insuranceStart()).isEqualTo(LocalDate.of(2026, 8, 1));
		assertThat(application.iban()).isEqualTo("DE89370400440532013000");
		assertThat(application.premium()).isEqualTo(Money.euro("31.80"));
		assertThat(application.applicant().salutation()).isEqualTo(Salutation.FRAU);
		assertThat(application.applicant().fullName()).isEqualTo("Erika Mustermann");
		assertThat(application.applicant().city()).isEqualTo("Köln");
		assertThat(application.insuredPersons())
				.extracting(InsuredPerson::birthDate)
				.containsExactly(LocalDate.of(1990, 5, 15), LocalDate.of(2015, 9, 20));
	}
}
