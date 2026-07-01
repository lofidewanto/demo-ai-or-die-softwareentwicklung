package com.example.taa.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigInteger;

/**
 * IBAN validator: checks the overall structure and the ISO 7064 mod-97-10
 * check digits. Whitespace is ignored; blank input is treated as valid (use
 * {@code @NotBlank} for the required check).
 */
public class IbanValidator implements ConstraintValidator<Iban, String> {

	private static final BigInteger MOD = BigInteger.valueOf(97);

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			return true;
		}
		String normalized = value.replaceAll("\\s", "").toUpperCase();

		// Length 15..34, starts with two letters (country) + two check digits.
		if (!normalized.matches("[A-Z]{2}\\d{2}[A-Z0-9]{11,30}")) {
			return false;
		}

		// Move the first four characters to the end, then convert letters to numbers.
		String rearranged = normalized.substring(4) + normalized.substring(0, 4);
		StringBuilder numeric = new StringBuilder(rearranged.length() * 2);
		for (char c : rearranged.toCharArray()) {
			if (Character.isLetter(c)) {
				numeric.append(c - 'A' + 10);
			} else {
				numeric.append(c);
			}
		}
		return new BigInteger(numeric.toString()).mod(MOD).intValue() == 1;
	}
}
