
package security;

import crmapi.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Dave Syer
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, ApplicationTests.ExtraConfig.class},
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

	@Autowired
	TestRestTemplate testRestTemplate;

	@Test
	public void passwordGrant() {
		MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
		request.set("username", "jlong");
		request.set("password", "password");
		request.set("grant_type", "password");
		Map<String, Object> token = testRestTemplate
			.postForObject("/oauth/token", request, Map.class);
		assertNotNull("Wrong response: " + token, token.get("access_token"));
	}

	@TestConfiguration
	public static class ExtraConfig {

		@Bean
		RestTemplateBuilder restTemplateBuilder() {
			return new RestTemplateBuilder()
				.basicAuthorization("android-crmapi", "123456");
		}
	}

}
