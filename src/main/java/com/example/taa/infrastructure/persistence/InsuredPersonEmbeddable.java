package com.example.taa.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

/**
 * Embeddable value for an insured person, persisted in the child table
 * {@code travel_insured_person} via an {@code @ElementCollection}.
 */
@Embeddable
public class InsuredPersonEmbeddable {

	@Column(name = "birth_date", nullable = false)
	private LocalDate birthDate;

	protected InsuredPersonEmbeddable() {
		// required by JPA
	}

	public InsuredPersonEmbeddable(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}
}
