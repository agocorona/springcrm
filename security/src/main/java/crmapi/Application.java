package crmapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import crmapi.storage.StorageService;


// tag::code[]
//
// curl -X POST -vu android-crmapi:123456 http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=password&username=jlong&grant_type=password&scope=write&client_secret=123456&client_id=android-crmapi"
// curl -v POST http://127.0.0.1:8080/crmapi -H "Authorization: Bearer <oauth_token>""

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(AccountRepository accountRepository,
			CustomerRepository customerRepository,
			StorageService storageService) {
		storageService.init();

		return (evt) -> Arrays.asList(
			
				"jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(","))
				.forEach(
						a -> {
							Account account = accountRepository.save(new Account(a,
									"password",false));
							customerRepository.save(new Customer(
							 		a+"Customer1", "A description"));
							customerRepository.save(new Customer(
							 	    a+"Customer2", "A description"));
						});


		
	}

}



// end::code[]
