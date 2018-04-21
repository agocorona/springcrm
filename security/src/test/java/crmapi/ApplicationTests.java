package crmapi;

import crmapi.Application;

import org.junit.Before;
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



import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;





 
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, ApplicationTests.ExtraConfig.class},
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
	private MediaType  contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
					MediaType.APPLICATION_JSON.getSubtype(),
					Charset.forName("utf8"));

	
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	private MockMvc mockMvc;

	private Account account;

	private String userName1 = "userName1";
	private String userName2 = "userName2";
	private String customerName1 = "customerName1";
	private String customerName2 = "customerName2";
	
	
	
	private String admin = "admin";

	

	private List<Customer> customerList = new ArrayList<>();

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

	@Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.customerRepository.deleteAllInBatch();
        this.accountRepository.deleteAllInBatch(); // not deleted since we need the user "jlong"

		this.account = accountRepository.save(new Account(userName1, "password",false));
		this.account = accountRepository.save(new Account(userName2, "password",false));
		this.account = accountRepository.save(new Account(admin, "password",false));


        this.customerList.add(customerRepository.save(new Customer(customerName1, "Surname")));
        this.customerList.add(customerRepository.save(new Customer(customerName2, "Surname")));
	
	}



	@Test
    public void listCustomers() throws Exception {
		String accessToken = obtainAccessToken(userName1, "password");
		mockMvc.perform(get("/crmapi/customers")
		        .header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id", is(this.customerList.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(customerName1)))
                .andExpect(jsonPath("$[0].surname", is("Surname")))
                .andExpect(jsonPath("$[1].id", is(this.customerList.get(1).getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(customerName2)))
                .andExpect(jsonPath("$[1].surname", is("Surname")));
    }
				
    
	@Test
    public void addCustomer() throws Exception {
		String accessToken = obtainAccessToken(userName1, "password");
        String customerJson= json(new Customer("testCustomer", "test Surmane"));

		this.mockMvc.perform(post("/crmapi/customers/add")
		        .header("Authorization", "Bearer " + accessToken)
                .contentType(contentType)
				.content(customerJson))
                .andExpect(status().isOk());
	}

	
	@Test
    public void modifyCustomer() throws Exception {
		String accessToken = obtainAccessToken(userName2, "password");
		Customer cust= customerRepository.findByName(customerName1).get();
		cust.setSurname("Surname changed");
		String customerJson= json(cust);

		this.mockMvc.perform(post("/crmapi/customers/modify")
		        .header("Authorization", "Bearer " + accessToken)
                .contentType(contentType)
				.content(customerJson))
				.andExpect(status().isOk());

		Long userName2Id= accountRepository.findByUsername(userName2).get().getId();

        Customer newCustomerReg= customerRepository.findByName(customerName1).get();
		assertEquals("Surname changed", newCustomerReg.getSurname());
		assertEquals(userName2Id, newCustomerReg.getCreatedBy());

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
	
	private String obtainAccessToken(String userName1, String password) throws Exception {
		
			MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
			request.set("username", userName1);
			request.set("password", password);
			request.set("grant_type", "password");
			Map<String, String> token = testRestTemplate
				.postForObject("/oauth/token", request, Map.class);
			assertNotNull("Wrong response: " + token, token.get("access_token"));
			System.out.println(token);
	        return (token.get("access_token"));
	}

}
