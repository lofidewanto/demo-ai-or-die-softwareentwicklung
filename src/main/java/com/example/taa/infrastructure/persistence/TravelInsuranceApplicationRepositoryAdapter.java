package com.example.taa.infrastructure.persistence;

import com.example.taa.domain.model.TravelInsuranceApplication;
import com.example.taa.domain.repository.TravelInsuranceApplicationRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter implementing the domain {@link TravelInsuranceApplicationRepository}
 * port on top of Spring Data JPA. Translates between the domain aggregate and
 * the JPA entity.
 */
@Repository
public class TravelInsuranceApplicationRepositoryAdapter
		implements TravelInsuranceApplicationRepository {

	private final TravelInsuranceApplicationJpaRepository jpaRepository;

	public TravelInsuranceApplicationRepositoryAdapter(
			TravelInsuranceApplicationJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public TravelInsuranceApplication save(TravelInsuranceApplication application) {
		TravelInsuranceApplicationEntity entity = TravelInsuranceApplicationMapper.toEntity(application);
		TravelInsuranceApplicationEntity saved = jpaRepository.save(entity);
		return TravelInsuranceApplicationMapper.toDomain(saved);
	}

	@Override
	public Optional<TravelInsuranceApplication> findById(Long id) {
		return jpaRepository.findById(id).map(TravelInsuranceApplicationMapper::toDomain);
	}
}
