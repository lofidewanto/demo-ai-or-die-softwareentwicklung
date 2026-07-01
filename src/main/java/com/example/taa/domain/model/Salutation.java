package com.example.taa.domain.model;

/**
 * Anrede (salutation) of a person, following the DEVK wording.
 */
public enum Salutation {

	FRAU("Frau"),
	HERR("Herr"),
	DIVERS("Divers");

	private final String label;

	Salutation(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
