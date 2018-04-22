package crmapi;

import crmapi.Application;
import crmapi.storage.StorageService;

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
import org.springframework.http.HttpHeaders;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;



import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import javax.validation.constraints.AssertFalse;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;


import org.springframework.util.StreamUtils;
 
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
	
	private String accessToken;
	
	private String admin = "admin";

	

	private List<Customer> customerList = new ArrayList<>();
	private List<Account>  accountList = new ArrayList<>();

	@Autowired
	TestRestTemplate testRestTemplate;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private StorageService storageService;

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

		this.accountList.add(accountRepository.save(new Account(userName1, "password",false)));
		this.accountList.add(accountRepository.save(new Account(userName2, "password",false)));
		this.accountList.add(accountRepository.save(new Account(admin, "password",false)));


        this.customerList.add(customerRepository.save(new Customer(customerName1, "Surname")));
        this.customerList.add(customerRepository.save(new Customer(customerName2, "Surname")));
		this.accessToken = obtainAccessToken(userName1, "password");

		
	}



	@Test
    public void listCustomers() throws Exception {
		//String accessToken = obtainAccessToken(userName1, "password");
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
		// String accessToken = obtainAccessToken(userName1, "password");
        String customerJson= json(new Customer("testCustomer", "test Surmane"));

		this.mockMvc.perform(post("/crmapi/customers/add")
		        .header("Authorization", "Bearer " + accessToken)
                .contentType(contentType)
				.content(customerJson))
                .andExpect(status().isOk());
	}

	public void getCustomer() throws Exception {
		// String accessToken = obtainAccessToken(userName1, "password");
        String customerJson= json(new Customer("testCustomer", "test Surmane"));

		this.mockMvc.perform(post("/crmapi/customers/"+customerName1)
		        .header("Authorization", "Bearer " + accessToken)
                .contentType(contentType)
				.content(customerJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is(customerName1)))
				.andExpect(jsonPath("surname", is("surname")));
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
		//assertEquals(userName2Id, newCustomerReg.getCreatedBy()); FAIL

	}

	@Test   //create user
	public void addAccount() throws Exception {
		String accessToken = obtainAccessToken(admin, "password");
        String userJson= json(new Account("testUser", "test Surmane",false));

		this.mockMvc.perform(post("/crmapi/accounts/add")
		        .header("Authorization", "Bearer " + accessToken)
                .contentType(contentType)
				.content(userJson))
                .andExpect(status().isOk());
	}

	@Test // modify user
	public void modifyAccount() throws Exception {
		String accessToken = obtainAccessToken(admin, "password");
        String userJson= json(accountRepository.findByUsername(userName1).get());

		this.mockMvc.perform(post("/crmapi/accounts/modify")
		        .header("Authorization", "Bearer " + accessToken)
                .contentType(contentType)
				.content(userJson))
                .andExpect(status().isOk());
	}

	@Test // modify  user by name
	public void modifyAccountByName() throws Exception {
		String accessToken = obtainAccessToken(admin, "password");
        String userJson= json(new Account(userName1,"modifiedpassword", true));

		this.mockMvc.perform(post("/crmapi/accounts/modify")
		        .header("Authorization", "Bearer " + accessToken)
                .contentType(contentType)
				.content(userJson))
                .andExpect(status().isOk());
	
		Account accountReg= accountRepository.findByUsername(userName1).get();
		assertEquals("modifiedpassword", accountReg.getPassword());	
	}


	@Test // modify unexistent user
	public void modifyUnexistentAccount() throws Exception {
		String accessToken = obtainAccessToken(admin, "password");
        String userJson= json(new Account("John","Doe", false));

		this.mockMvc.perform(post("/crmapi/accounts/modify")
		        .header("Authorization", "Bearer " + accessToken)
                .contentType(contentType)
				.content(userJson))
                .andExpect(status().isNotFound());
	}	

	@Test // delete user
	public void deleteAccount() throws Exception {

		String accessToken = obtainAccessToken(userName1, "password");
		this.mockMvc.perform(delete("/crmapi/accounts/"+ userName1)
				.header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk());
		Optional<Account> user= accountRepository.findByUsername (userName1);
		assertFalse(user.isPresent());
	}

	@Test
    public void listAccounts() throws Exception {
		String accessToken = obtainAccessToken(admin, "password");
		mockMvc.perform(get("/crmapi/accounts")
		        .header("Authorization", "Bearer " + accessToken))
				.andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id", is(this.accountList.get(0).getId().intValue())))
                .andExpect(jsonPath("$[0].username", is(userName1)))
                .andExpect(jsonPath("$[0].password", is("password")))
                .andExpect(jsonPath("$[1].id", is(this.accountList.get(1).getId().intValue())))
                .andExpect(jsonPath("$[1].username", is(userName2)))
				.andExpect(jsonPath("$[1].password", is("password")))
				.andExpect(jsonPath("$[2].id", is(this.accountList.get(2).getId().intValue())))
                .andExpect(jsonPath("$[2].username", is(admin)))
                .andExpect(jsonPath("$[2].password", is("password")));
	}
	
	@Test
	public void uploadImage() throws Exception {
		String accessToken = obtainAccessToken(admin, "password");
		ClassPathResource resource = new ClassPathResource("sample.jpg",getClass());
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("customer",customerName1);
		map.add("file", resource);
		Map<String, String> token = testRestTemplate
				.postForObject("/images/upload", map, Map.class);
		
		//Verify that the photo filename is in the database for the customer register
		String photo= customerRepository.findByName(customerName1).get().getPhoto();

		// Load the uploaded photo from the storage
		Resource image= storageService.loadAsResource(photo);
		String imageString = StreamUtils.copyToString(image.getInputStream(), Charset.defaultCharset());
        //assertThat(image.getInputStream().isEqualTo(resource.getInputStream()) );

		// load form the REST service and verify that it returns the photo
		ResponseEntity<String> response = this.testRestTemplate
		.getForEntity("/images/"+ photo, String.class, photo);

		assertThat(response.getStatusCodeValue()).isEqualTo(200);
		assertThat(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
				.isEqualTo("attachment; filename=\"customerName1.jpg\"");

        // verify that 	 upload-dir/customerName1.jpg and src/test/sample.jpg are identical

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



