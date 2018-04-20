/*
 * Copyright 2013-2104 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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





 
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, ApplicationTests.ExtraConfig.class},
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
					MediaType.APPLICATION_JSON.getSubtype(),
					Charset.forName("utf8"));
	
	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	private MockMvc mockMvc;

	private Account account;

	private String userName = "bdussault";

	private List<Customer> customerList = new ArrayList<>();

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
    private WebApplicationContext webApplicationContext;

	@Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.customerRepository.deleteAllInBatch();
    //    this.accountRepository.deleteAllInBatch(); // not deleted since we need the user "jlong"

        this.account = accountRepository.save(new Account(userName, "password",false));
        this.customerList.add(customerRepository.save(new Customer(userName+ "Customer1", "A description")));
        this.customerList.add(customerRepository.save(new Customer(userName+ "Customer2", "A description")));
	
	}

	// @Test
	// public void passwordGrant() {
	// 	MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
	// 	request.set("username", "jlong");
	// 	request.set("password", "password");
	// 	request.set("grant_type", "password");
	// 	Map<String, Object> token = testRestTemplate
	// 		.postForObject("/oauth/token", request, Map.class);
	// 	assertNotNull("Wrong response: " + token, token.get("access_token"));
	// }


	@Test
    public void listCustomers() throws Exception {
		String accessToken = obtainAccessToken(userName, "password");

		mockMvc.perform(get("/crmapi/customers")
		        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
	// @Test
    // public void addCustomer() throws Exception {
	// 	String accessToken = obtainAccessToken(userName, "password");

	// 	mockMvc.perform(post("/crmapi/customers")
	// 	        .header("Authorization", "Bearer " + accessToken)
    //             .content(json(new Customer("testCustomer", "test Surmane")))
    //             .contentType(contentType))
    //             .andExpect(status().isOk());
    // }


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
	
	private String obtainAccessToken(String username, String password) throws Exception {
		
			MultiValueMap<String, String> request = new LinkedMultiValueMap<String, String>();
			request.set("username", username);
			request.set("password", password);
			request.set("grant_type", "password");
			Map<String, String> token = testRestTemplate
				.postForObject("/oauth/token", request, Map.class);
			assertNotNull("Wrong response: " + token, token.get("access_token"));
			System.out.println(token);
	        return (token.get("access_token"));
	}

}
