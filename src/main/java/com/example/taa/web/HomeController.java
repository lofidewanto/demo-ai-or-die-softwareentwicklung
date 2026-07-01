package com.example.taa.web;

import com.example.taa.domain.model.Money;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the product landing page ("Anleitung"), which presents the DEVK
 * Auslandsreisekrankenversicherung and is the entry point into the online
 * application process.
 */
@Controller
public class HomeController {

	@GetMapping("/")
	public String home(Model model) {
		// Publicly documented starting premium, shown on the landing page.
		model.addAttribute("startingPremium", Money.euro("15.90").format());
		return "index";
	}
}
