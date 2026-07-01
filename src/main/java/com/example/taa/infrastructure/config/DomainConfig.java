package com.example.taa.infrastructure.config;

import com.example.taa.domain.service.PremiumCalculator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Wires the framework-free domain services as Spring beans.
 *
 * <p>The domain classes intentionally carry no Spring annotations (so they stay
 * independent of the framework and easy to unit test); they are exposed as beans
 * here instead.
 */
@Configuration(proxyBeanMethods = false)
public class DomainConfig {

	@Bean
	public PremiumCalculator premiumCalculator() {
		return new PremiumCalculator();
	}

	/**
	 * A {@link Clock} bean so timestamps can be controlled in tests.
	 */
	@Bean
	@ConditionalOnMissingBean
	public Clock clock() {
		return Clock.systemDefaultZone();
	}
}
