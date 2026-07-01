package com.example.taa.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Adds a small, dependency-free set of security-related HTTP response headers,
 * mirroring the hardening applied by the DEVK reference application.
 *
 * <p>For this public, unauthenticated demo a full Spring Security setup would be
 * disproportionate (see the implementation plan). Output escaping (Thymeleaf),
 * strict server-side validation and these headers provide sensible baseline
 * hardening.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter extends OncePerRequestFilter {

	private static final String CONTENT_SECURITY_POLICY = String.join("; ",
			"default-src 'self'",
			"img-src 'self' data:",
			"style-src 'self'",
			"script-src 'self'",
			"form-action 'self'",
			"frame-ancestors 'none'",
			"base-uri 'self'");

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		response.setHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("X-Frame-Options", "DENY");
		response.setHeader("Referrer-Policy", "same-origin");
		response.setHeader("Content-Security-Policy", CONTENT_SECURITY_POLICY);

		filterChain.doFilter(request, response);
	}
}
