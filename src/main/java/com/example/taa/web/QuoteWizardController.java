package com.example.taa.web;

import jakarta.validation.Valid;

import com.example.taa.application.TravelInsuranceService;
import com.example.taa.application.command.SubmitApplicationCommand;
import com.example.taa.domain.model.Money;
import com.example.taa.domain.model.Salutation;
import com.example.taa.domain.model.TravelInsuranceApplication;
import com.example.taa.domain.service.PremiumCalculator;
import com.example.taa.web.form.ApplicationForm;
import com.example.taa.web.form.GeneralDetailsForm;
import com.example.taa.web.session.QuoteWizard;
import com.example.taa.web.support.WizardStep;
import com.example.taa.web.view.ConfirmationView;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Drives the multi-step online application process for the DEVK
 * Auslandsreisekrankenversicherung, mirroring the reference strecke:
 * Allgemeine Angaben &rarr; Unser Angebot &rarr; Ihr Antrag &rarr;
 * Ihr Versicherungsschein.
 *
 * <p>Uses the POST-Redirect-GET pattern; the per-session {@link QuoteWizard}
 * keeps the intermediate state and enforces the step order via guards.
 */
@Controller
@RequestMapping("/rechner")
public class QuoteWizardController {

	private final TravelInsuranceService travelInsuranceService;
	private final QuoteWizard wizard;

	public QuoteWizardController(TravelInsuranceService travelInsuranceService, QuoteWizard wizard) {
		this.travelInsuranceService = travelInsuranceService;
		this.wizard = wizard;
	}

	@ModelAttribute("salutations")
	public Salutation[] salutations() {
		return Salutation.values();
	}

	@ModelAttribute("steps")
	public WizardStep[] steps() {
		return WizardStep.values();
	}

	@ModelAttribute("maxPersons")
	public int maxPersons() {
		return PremiumCalculator.MAX_INSURED_PERSONS;
	}

	// --- Step 1: Allgemeine Angaben -------------------------------------------

	@GetMapping("/allgemeine-angaben")
	public String showGeneralDetails(Model model) {
		if (!model.containsAttribute("generalDetailsForm")) {
			GeneralDetailsForm form = wizard.getGeneralDetails() != null
					? wizard.getGeneralDetails()
					: new GeneralDetailsForm();
			model.addAttribute("generalDetailsForm", form);
		}
		model.addAttribute("activeStep", WizardStep.ALLGEMEINE_ANGABEN);
		return "rechner/allgemeine-angaben";
	}

	@PostMapping("/allgemeine-angaben")
	public String submitGeneralDetails(
			@Valid @ModelAttribute("generalDetailsForm") GeneralDetailsForm form,
			BindingResult bindingResult,
			Model model) {

		validateInsuredPersons(form, bindingResult);

		if (bindingResult.hasErrors()) {
			model.addAttribute("activeStep", WizardStep.ALLGEMEINE_ANGABEN);
			return "rechner/allgemeine-angaben";
		}

		Money premium = travelInsuranceService.calculatePremium(
				form.selectedBirthDates(), form.getInsuranceStart());
		wizard.storeQuote(form, premium);

		return "redirect:/rechner/angebot";
	}

	/**
	 * Enforces the requiredness of persons 2..N depending on the selected number
	 * of persons, attaching each error to its specific birth-date field.
	 */
	private void validateInsuredPersons(GeneralDetailsForm form, BindingResult bindingResult) {
		Integer count = form.getNumberOfPersons();
		if (count == null) {
			return; // already reported by @NotNull
		}
		for (int person = 1; person <= count && person <= PremiumCalculator.MAX_INSURED_PERSONS; person++) {
			String field = "birthDate" + person;
			if (form.birthDateForPerson(person) == null && !bindingResult.hasFieldErrors(field)) {
				bindingResult.rejectValue(field, "ark.birthDate.required.person",
						new Object[] {person}, "Bitte geben Sie das Geburtsdatum an.");
			}
		}
	}

	// --- Step 2: Unser Angebot ------------------------------------------------

	@GetMapping("/angebot")
	public String showOffer(Model model) {
		if (!wizard.hasQuote()) {
			return "redirect:/rechner/allgemeine-angaben";
		}
		model.addAttribute("generalDetails", wizard.getGeneralDetails());
		model.addAttribute("premium", wizard.getPremium().format());
		model.addAttribute("activeStep", WizardStep.ANGEBOT);
		return "rechner/angebot";
	}

	// --- Step 3: Ihr Antrag ---------------------------------------------------

	@GetMapping("/antrag")
	public String showApplication(Model model) {
		if (!wizard.hasQuote()) {
			return "redirect:/rechner/allgemeine-angaben";
		}
		if (!model.containsAttribute("applicationForm")) {
			model.addAttribute("applicationForm", new ApplicationForm());
		}
		addOfferSummary(model);
		model.addAttribute("activeStep", WizardStep.ANTRAG);
		return "rechner/antrag";
	}

	@PostMapping("/antrag")
	public String submitApplication(
			@Valid @ModelAttribute("applicationForm") ApplicationForm form,
			BindingResult bindingResult,
			Model model) {

		if (!wizard.hasQuote()) {
			return "redirect:/rechner/allgemeine-angaben";
		}
		if (bindingResult.hasErrors()) {
			addOfferSummary(model);
			model.addAttribute("activeStep", WizardStep.ANTRAG);
			return "rechner/antrag";
		}

		GeneralDetailsForm generalDetails = wizard.getGeneralDetails();
		SubmitApplicationCommand command = new SubmitApplicationCommand(
				generalDetails.getInsuranceStart(),
				generalDetails.selectedBirthDates(),
				form.getSalutation(),
				form.getFirstName(),
				form.getLastName(),
				form.getBirthDate(),
				form.getStreet(),
				form.getHouseNumber(),
				form.getPostalCode(),
				form.getCity(),
				form.getEmail(),
				form.getPhone(),
				form.getIban());

		TravelInsuranceApplication submitted = travelInsuranceService.submit(command);
		wizard.storeSubmission(submitted);

		return "redirect:/rechner/versicherungsschein";
	}

	// --- Step 4: Ihr Versicherungsschein --------------------------------------

	@GetMapping("/versicherungsschein")
	public String showConfirmation(Model model) {
		if (!wizard.isSubmitted()) {
			return "redirect:/rechner/angebot";
		}
		TravelInsuranceApplication application = wizard.getSubmittedApplication();
		model.addAttribute("confirmation",
				new ConfirmationView(application, application.premium().format()));
		model.addAttribute("activeStep", WizardStep.VERSICHERUNGSSCHEIN);
		return "rechner/versicherungsschein";
	}

	/** Starts a fresh calculation, clearing any previous session state. */
	@GetMapping("/neu")
	public String restart() {
		wizard.reset();
		return "redirect:/rechner/allgemeine-angaben";
	}

	private void addOfferSummary(Model model) {
		model.addAttribute("generalDetails", wizard.getGeneralDetails());
		model.addAttribute("premium", wizard.getPremium().format());
	}
}
