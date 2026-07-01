package com.example.taa.web.session;

import com.example.taa.domain.model.Money;
import com.example.taa.domain.model.TravelInsuranceApplication;
import com.example.taa.web.form.GeneralDetailsForm;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Holds the state of the multi-step application process for the current user
 * session (POST-Redirect-GET wizard).
 *
 * <p>Session-scoped so that data survives redirects between steps and users can
 * navigate back and forth. The controller uses the completion flags to guard the
 * step order.
 */
@Component
@SessionScope
public class QuoteWizard {

	private GeneralDetailsForm generalDetails;
	private Money premium;
	private TravelInsuranceApplication submittedApplication;

	public boolean hasQuote() {
		return generalDetails != null && premium != null;
	}

	public boolean isSubmitted() {
		return submittedApplication != null;
	}

	public void storeQuote(GeneralDetailsForm generalDetails, Money premium) {
		this.generalDetails = generalDetails;
		this.premium = premium;
		// A recalculation invalidates any previously submitted result.
		this.submittedApplication = null;
	}

	public void storeSubmission(TravelInsuranceApplication application) {
		this.submittedApplication = application;
	}

	public void reset() {
		this.generalDetails = null;
		this.premium = null;
		this.submittedApplication = null;
	}

	public GeneralDetailsForm getGeneralDetails() {
		return generalDetails;
	}

	public Money getPremium() {
		return premium;
	}

	public TravelInsuranceApplication getSubmittedApplication() {
		return submittedApplication;
	}
}
