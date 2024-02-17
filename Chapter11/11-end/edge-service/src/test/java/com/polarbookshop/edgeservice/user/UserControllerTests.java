package com.polarbookshop.edgeservice.user;

import java.util.List;

import com.polarbookshop.edgeservice.config.SecurityConfig;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(UserController.class)
@Import(SecurityConfig.class) // Import Security Config class
class UserControllerTests {

	@Autowired
	WebTestClient webClient;

	/*
	A mock bean to skip the interaction with Keycloak when retrieving information about the Client
	registration
	 */
	@MockBean
	ReactiveClientRegistrationRepository clientRegistrationRepository;

	@Test
	void whenNotAuthenticatedThen401() {
		webClient
				.get()
				.uri("/user")
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	void whenAuthenticatedThenReturnUser() {
		var expectedUser = new User("jon.snow", "Jon", "Snow", List.of("employee", "customer"));

		webClient
				// start - Defines an authentication context based on OIDC and uses the expected user
				.mutateWith(configureMockOidcLogin(expectedUser))
				.get()
				.uri("/user")
				.exchange()
				.expectStatus().is2xxSuccessful()
				// end
				.expectBody(User.class)
				.value(user -> assertThat(user).isEqualTo(expectedUser));
	}

	/*
	Builds a mock ID Token
	 */
	private SecurityMockServerConfigurers.OidcLoginMutator configureMockOidcLogin(User expectedUser) {
		return SecurityMockServerConfigurers.mockOidcLogin().idToken(builder -> {
			builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username());
			builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName());
			builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName());
		});
	}

}
