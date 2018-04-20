
package crmapi;

import org.junit.Before;
import crmapi.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;



import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;




/**
 * @author Dave Syer
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, ApplicationTests.ExtraConfig.class},
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
				MediaType.APPLICATION_JSON.getSubtype(),
				Charset.forName("utf8"));
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

	private Account account;
	private String userName = "bdussault";
	private List<Customer> customerList = new ArrayList<>();


	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	private CustomerRepository customerRepository;
	private AccountRepository accountRepository;

	@Autowired
    private WebApplicationContext webApplicationContext;

	@Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.customerRepository.deleteAllInBatch();
        this.accountRepository.deleteAllInBatch();

        this.account = accountRepository.save(new Account(userName, "password",false));
        this.customerList.add(customerRepository.save(new Customer(userName+ "Customer1", "A description")));
        this.customerList.add(customerRepository.save(new Customer(userName+ "Customer2", "A description")));
    }

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

	@Test
    public void userNotFound() throws Exception {
        mockMvc.perform(post("/crmapi/customers/")
                .content(this.json(new Customer(null, null)))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

	@TestConfiguration
	public static class ExtraConfig {

		@Bean
		RestTemplateBuilder restTemplateBuilder() {
			return new RestTemplateBuilder()
				.basicAuthorization("android-crmapi", "123456");
		}
	}
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
