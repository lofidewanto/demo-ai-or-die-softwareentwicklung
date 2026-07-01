package com.example.taa.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.taa.application.command.SubmitApplicationCommand;
import com.example.taa.domain.model.Money;
import com.example.taa.domain.model.Salutation;
import com.example.taa.domain.model.TravelInsuranceApplication;
import com.example.taa.domain.repository.TravelInsuranceApplicationRepository;
import com.example.taa.domain.service.PremiumCalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TravelInsuranceServiceTest {

	private static final Instant NOW = Instant.parse("2026-07-01T10:15:30Z");

	@Mock
	private TravelInsuranceApplicationRepository repository;

	private TravelInsuranceService service;

	@BeforeEach
	void setUp() {
		Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
		service = new TravelInsuranceService(new PremiumCalculator(), repository, clock);
	}

	@Test
	void calculatePremiumUsesTheDomainTariff() {
		Money premium = service.calculatePremium(
				List.of(LocalDate.of(1990, 5, 15)), LocalDate.of(2026, 8, 1));

		assertThat(premium).isEqualTo(Money.euro("15.90"));
	}

	@Test
	void submitBuildsAggregateCalculatesPremiumNormalizesIbanAndPersists() {
		when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		SubmitApplicationCommand command = new SubmitApplicationCommand(
				LocalDate.of(2026, 8, 1),
				List.of(LocalDate.of(1990, 5, 15), LocalDate.of(1992, 3, 3)),
				Salutation.FRAU,
				"Erika",
				"Mustermann",
				LocalDate.of(1990, 5, 15),
				"Musterstraße",
				"1",
				"50735",
				"Köln",
				"erika@example.com",
				"0221 123456",
				"de89 3704 0044 0532 0130 00");

		TravelInsuranceApplication result = service.submit(command);

		ArgumentCaptor<TravelInsuranceApplication> captor =
				ArgumentCaptor.forClass(TravelInsuranceApplication.class);
		verify(repository).save(captor.capture());
		TravelInsuranceApplication saved = captor.getValue();

		assertThat(saved.premium()).isEqualTo(Money.euro("31.80"));
		assertThat(saved.insuredPersons()).hasSize(2);
		assertThat(saved.iban()).isEqualTo("DE89370400440532013000");
		assertThat(saved.submittedAt()).isEqualTo(NOW);
		assertThat(saved.applicant().fullName()).isEqualTo("Erika Mustermann");
		assertThat(result.premium()).isEqualTo(Money.euro("31.80"));
	}
}
