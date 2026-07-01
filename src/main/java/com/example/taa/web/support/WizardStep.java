package com.example.taa.web.support;

/**
 * The steps of the online application process, mirroring the DEVK reference
 * strecke ("Ihre Eingaben"). Used to render the progress stepper.
 */
public enum WizardStep {

	ALLGEMEINE_ANGABEN("Allgemeine Angaben"),
	ANGEBOT("Unser Angebot"),
	ANTRAG("Ihr Antrag"),
	VERSICHERUNGSSCHEIN("Ihr Versicherungsschein");

	private final String label;

	WizardStep(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	/** 1-based number for display. */
	public int getNumber() {
		return ordinal() + 1;
	}
}
