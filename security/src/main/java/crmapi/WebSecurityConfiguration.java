
package crmapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



// tag::code[]
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	AccountRepository accountRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Bean
	UserDetailsService userDetailsService() {
		return (username) -> accountRepository
				.findByUsername(username)
				.map(a -> new User(a.getUsername(), a.getPassword(), true, true, true, true,
						AuthorityUtils.createAuthorityList("USER", "write")))
				.orElseThrow(
						() -> new UsernameNotFoundException("could not find the user '"
								+ username + "'"));
	}
}


// class SpringSecurityAuditorAware implements AuditorAware<String> {
// 	@Autowired
// 	AccountRepository accountRepository;

// 	public String getCurrentAuditor() {
  
// 	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
  
// 	  if (authentication == null || !authentication.isAuthenticated()) {
// 		return null;
// 	  }
// 	  return ((User) authentication.getPrincipal()).getUsername();
// 	}
//   }

//   @Configuration
//   @EnableJpaAuditing
//   class Config {
  
// 	@Bean
// 	public AuditorAware<String> auditorProvider() {
// 	  return new SpringSecurityAuditorAware();
// 	}
//   }
// end::code[]
