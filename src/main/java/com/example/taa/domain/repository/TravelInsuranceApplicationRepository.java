package com.example.taa.domain.repository;

import com.example.taa.domain.model.TravelInsuranceApplication;

import java.util.Optional;

/**
 * Outbound port for persisting travel insurance applications.
 *
 * <p>Defined in the domain layer and implemented by an adapter in the
 * infrastructure layer (Ports &amp; Adapters). This keeps the domain and
 * application layers free of any persistence technology.
 */
public interface TravelInsuranceApplicationRepository {

	/**
	 * Persists the given (new) application and returns the stored aggregate
	 * including its assigned technical id.
	 */
	TravelInsuranceApplication save(TravelInsuranceApplication application);

	Optional<TravelInsuranceApplication> findById(Long id);
}
