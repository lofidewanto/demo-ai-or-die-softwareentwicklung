package com.example.taa.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link TravelInsuranceApplicationEntity}.
 *
 * <p>This is an internal infrastructure detail; the rest of the application
 * depends on the domain port {@code TravelInsuranceApplicationRepository}.
 */
public interface TravelInsuranceApplicationJpaRepository
		extends JpaRepository<TravelInsuranceApplicationEntity, Long> {
}
