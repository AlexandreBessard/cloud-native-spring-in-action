package com.polarbookshop.edgeservice.config;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.WebFilter;

@EnableWebFluxSecurity //Webflux  support
public class SecurityConfig {

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
		return http
				.authorizeExchange(exchange -> exchange
						// Access policies
						.pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
					 	.pathMatchers(HttpMethod.GET, "/books/**").permitAll()
						// Other requests need to be authenticated
						.anyExchange().authenticated()
				)
				// When an exception is thrown because a user is not authenticated, it replies with an HTTP 401 response.
				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
				// Enables user authentication with OAuth2/OpenID Connect
				.oauth2Login(Customizer.withDefaults())
				// logout mechanism
				.logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
				.csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
				.build();
	}

	private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
		var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
		oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
		return oidcLogoutSuccessHandler;
	}

	@Bean
	WebFilter csrfWebFilter() {
		// Required because of https://github.com/spring-projects/spring-security/issues/5766
		return (exchange, chain) -> {
			exchange.getResponse().beforeCommit(() -> Mono.defer(() -> {
				Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
				return csrfToken != null ? csrfToken.then() : Mono.empty();
			}));
			return chain.filter(exchange);
		};
	}

}
