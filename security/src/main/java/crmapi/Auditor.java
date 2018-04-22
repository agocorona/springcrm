package crmapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;


import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


class SpringSecurityAuditorAware implements AuditorAware<String> {
	@Autowired
	AccountRepository accountRepository;

	public String getCurrentAuditor() {
  
	  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
  
	  if (authentication == null || !authentication.isAuthenticated()) {
		return null;
	  }
	  return ((User) authentication.getPrincipal()).getUsername();
	}
  }

  @Configuration
  @EnableJpaAuditing
  class Config {
  
	@Bean
	public AuditorAware<String> auditorProvider() {
	  return new SpringSecurityAuditorAware();
	}
  }