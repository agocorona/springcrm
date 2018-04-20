
package crmapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;


// tag::code[]
@RestController
@RequestMapping("/crmapi")
class CustomerRestController {

	private final CustomerRepository customerRepository;

	private final AccountRepository accountRepository;

	@Autowired
	CustomerRestController(CustomerRepository customerRepository,
						   AccountRepository accountRepository) {
		this.customerRepository = customerRepository;
		this.accountRepository = accountRepository;
	}

	//A user can only:
	// • List all customers in the database.
	@RequestMapping(value="/customers",method = RequestMethod.GET)
	Resources<CustomerResource> readCustomers(Principal principal) {
		this.validateUser(principal);

		List<CustomerResource> customerResourceList = customerRepository
			.findAll().stream()
			.map(CustomerResource::new)
			.collect(Collectors.toList());

		return new Resources<>(customerResourceList);
	}
	// • Get full customer information, ***including a photo URL***.
	@RequestMapping(value="/customers/{customer}",method = RequestMethod.GET)
	CustomerResource readCustomer(Principal principal,  @PathVariable String customer) {
		this.validateUser(principal);
		CustomerResource customerResourceReg=  new CustomerResource(customerRepository.findByName(customer));
		return(customerResourceReg);
	}
	// • Create a new customer:
	// • A customer should have at least name, surname, id and a photo field.
	// • Name, surname and id are required fields. x
	// • Image uploads should be able to be managed.

	@RequestMapping(value="/customers/add",method = RequestMethod.POST)
	CustomerResource addCustomer(Principal principal, @RequestBody Customer input) {
		this.validateUser(principal);

		Customer customer = customerRepository.save(
						new Customer(input.getName(), input.getSurname()));


		return new CustomerResource(customer);
	}


	// • Update an existing customer.
	// • The customer should hold a reference to the last user who modified it.
	@RequestMapping(value="/customers",method = RequestMethod.POST)
    CustomerResource modifyCustomer(Principal principal, @RequestBody Customer input) {
		this.validateUser(principal);

		Customer customer = customerRepository.findByName(input.getName());
		customerRepository.save(new Customer(customer.getName(),input.getSurname()));
							
		return new CustomerResource(customer);

	}
	// • Delete an existing customer.

	@RequestMapping(value="/customers/{customer}",method = RequestMethod.DELETE)
    void deleteCustomer(Principal principal, @PathVariable String customer) {
		this.validateUser(principal);
        Customer customerReg = customerRepository.findByName(customer);
		customerRepository.delete(customerReg.getId());

	}



	// • An admin can also:
	// • Create users.
	@RequestMapping(value="/accounts",method = RequestMethod.POST)
	Account createUser(Principal principal,  @RequestBody Account input) {
		this.validateUserAdmin(principal);
		Account accountReg= 
		    accountRepository.save( new Account(input.getUsername(),"password",input.getIsAdmin()));
		return(accountReg);
	}
	// • Delete users.
	@RequestMapping(value="/accounts/{username}",method = RequestMethod.DELETE)
    void deleteAccount(Principal principal, @PathVariable String username) {
		this.validateUserAdmin(principal);
		Optional<Account>  accountReg= accountRepository.findByUsername(username);
		if (!accountReg.isPresent()) throw new UserNotFoundException(username);
		customerRepository.delete(accountReg.get().getId());

	}

	// • Update users.

	@RequestMapping(value="/accounts",method = RequestMethod.PUT)
    Account modifyAccount(Principal principal, @RequestBody Account input) {
		this.validateUserAdmin(principal);
		Optional<Account>  accountReg= accountRepository.findByUsername(input.getUsername());
		if (!accountReg.isPresent()) throw new UserNotFoundException(principal.getName());
		accountRepository.save(new Account(accountReg.get().getUsername()
										  ,input.getPassword()
										  ,input.getIsAdmin()));
							
		return accountReg.get();

	}
	// • List users.
	@RequestMapping(value="/accounts",method = RequestMethod.GET)
	Resources<Account> readAcounts(Principal principal) {
		this.validateUserAdmin(principal);

		List<Account> accountList = accountRepository
			.findAll().stream()
			.collect(Collectors.toList());

		return  new Resources<>(accountList);
	}
	// • Change admin status.

	@RequestMapping(value="/accounts/switch",method = RequestMethod.POST)
    Account modifyAdminStatus(Principal principal, @RequestBody Account input) {
		this.validateUserAdmin(principal);
		Optional<Account>  accountReg= accountRepository.findByUsername(input.getUsername());
		if (!accountReg.isPresent()) throw new UserNotFoundException(principal.getName());
		boolean modified= ! accountReg.get().getIsAdmin();
		accountRepository.save(new Account(accountReg.get().getUsername(),input.getPassword(),modified));
							
		return accountReg.get();

	}






	private void validateUser(Principal principal) {
		String userId = principal.getName();
		this.accountRepository
			.findByUsername(userId)
			.orElseThrow(
				() -> new UserNotFoundException(userId));
	}
	private void validateUserAdmin(Principal principal) {
		String userId = principal.getName();
		Account acc= this.accountRepository
					 .findByUsername(userId).orElseThrow(
						() -> new UserNotFoundException(userId));
					 
		Boolean isAdmin= acc.getIsAdmin();
	    if (!isAdmin) throw( new AccessDeniedException(userId));
	}
}
// end::code[]
