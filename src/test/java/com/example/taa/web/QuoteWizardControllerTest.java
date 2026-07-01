package com.example.taa.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.taa.application.TravelInsuranceService;
import com.example.taa.domain.model.Money;
import com.example.taa.web.session.QuoteWizard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

@WebMvcTest(QuoteWizardController.class)
@Import(QuoteWizard.class)
class QuoteWizardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TravelInsuranceService travelInsuranceService;

	@Test
	void showsGeneralDetailsForm() throws Exception {
		mockMvc.perform(get("/rechner/allgemeine-angaben"))
				.andExpect(status().isOk())
				.andExpect(view().name("rechner/allgemeine-angaben"))
				.andExpect(model().attributeExists("generalDetailsForm", "steps"));
	}

	@Test
	void rejectsInvalidGeneralDetailsAndRedisplaysForm() throws Exception {
		mockMvc.perform(post("/rechner/allgemeine-angaben")
						.param("numberOfPersons", "1")) // missing start date and birth date
				.andExpect(status().isOk())
				.andExpect(view().name("rechner/allgemeine-angaben"))
				.andExpect(model().attributeHasFieldErrors(
						"generalDetailsForm", "insuranceStart", "birthDate1"));
	}

	@Test
	void acceptsValidGeneralDetailsAndRedirectsToOffer() throws Exception {
		when(travelInsuranceService.calculatePremium(anyList(), any(LocalDate.class)))
				.thenReturn(Money.euro("15.90"));

		mockMvc.perform(post("/rechner/allgemeine-angaben")
						.param("insuranceStart", LocalDate.now().plusDays(7).toString())
						.param("numberOfPersons", "1")
						.param("birthDate1", "1990-05-15"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rechner/angebot"));

		verify(travelInsuranceService).calculatePremium(anyList(), any(LocalDate.class));
	}

	@Test
	void offerStepRedirectsWhenNoQuoteInSession() throws Exception {
		mockMvc.perform(get("/rechner/angebot"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rechner/allgemeine-angaben"));
	}

	@Test
	void confirmationStepRedirectsWhenNothingSubmitted() throws Exception {
		mockMvc.perform(get("/rechner/versicherungsschein"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/rechner/angebot"));
	}
}
