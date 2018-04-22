
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
import org.springframework.transaction.annotation.Transactional;


import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Collection;


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
	Collection<Customer> readCustomers(Principal principal) {
		this.validateUser(principal);

		return customerRepository.findAll();
		// 	.stream()
		// 	.map(CustomerResource::new)
		// 	.collect(Collectors.toList());

		// return new Resources<>(customerResourceList);
	}
	// • Get full customer information, ***including a photo URL***.
	@RequestMapping(value="/customers/{customer}",method = RequestMethod.GET)
	CustomerResource readCustomer(Principal principal,  @PathVariable String customer) {
		this.validateUser(principal);
		CustomerResource customerResourceReg=  
		   new CustomerResource(customerRepository
		      .findByName(customer).orElseThrow(
				() -> new CustomerNotFoundException(customer)));
		return(customerResourceReg);
	}
	// • Create a new customer:
	// • A customer should have at least name, surname, id and a photo field.
	// • Name, surname and id are required fields. x
	// • Image uploads should be able to be managed.

	@RequestMapping(value="/customers/add",method = RequestMethod.POST)
	void addCustomer(Principal principal, @RequestBody Customer input) {
		this.validateUser(principal);
		customerRepository.save(input);
						//new Customer(input.getName(), input.getSurname()));


	}


	// • Update an existing customer.
	// • The customer should hold a reference to the last user who modified it.
	@RequestMapping(value="/customers/modify",method = RequestMethod.POST)
	@Transactional
    void modifyCustomer(Principal principal, @RequestBody Customer input) {
		this.validateUser(principal);

		// Customer customer = customerRepository.findByName(input.getName()).orElseThrow(
		// 	() -> new UserNotFoundException(input.getName()));
		Customer customerToUpdate=customerRepository.findById(input.getId()).orElseThrow(
			   	() -> new CustomerNotFoundException(input.getName()));
		customerToUpdate.setName(input.getName());
		customerToUpdate.setSurname(input.getSurname());
		customerRepository.save(customerToUpdate);
							
	}
	@RequestMapping(value="/customers/modifytest/{customer}",method = RequestMethod.GET)
	@Transactional
    void modifyCustomer(Principal principal,@PathVariable String customer) {
		this.validateUser(principal);

		// Customer customer = customerRepository.findByName(input.getName()).orElseThrow(
		// 	() -> new UserNotFoundException(input.getName()));
		Customer customerToUpdate=customerRepository.findByName(customer).orElseThrow(
			   	() -> new CustomerNotFoundException(customer));
		customerToUpdate.setSurname("changed");
		customerRepository.save(customerToUpdate);
							
	}
	// • Delete an existing customer.

	@RequestMapping(value="/customers/{customer}",method = RequestMethod.DELETE)
    void deleteCustomer(Principal principal, @PathVariable String customer) {
		this.validateUser(principal);
		Customer customerReg = customerRepository
		                      .findByName(customer).orElseThrow(
								() -> new CustomerNotFoundException(customer));
		customerRepository.delete(customerReg.getId());

	}



	// • An admin can also:
	// • Create users.
	@RequestMapping(value="/accounts/add",method = RequestMethod.POST)
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
		accountRepository.delete(accountReg.get().getId());

	}

	// • Update users.
	// • Also changes admin status
	@RequestMapping(value="/accounts/modify",method = RequestMethod.POST)
	@Transactional
    void modifyAccount(Principal principal, @RequestBody Account input) {
		this.validateUserAdmin(principal);
		Account  accountReg= accountRepository
						.findById(input.getId())
						.orElseGet(() -> accountRepository.findByUsername(input.getUsername())
						.orElseThrow( () -> new UserNotFoundException(input.getUsername())));
		accountReg.setUsername(input.getUsername());
		accountReg.setPassword(input.getPassword());
		accountReg.setAdminState(input.getIsAdmin());
		
		accountRepository.save(accountReg);
							

	}
	// • List users.
	@RequestMapping(value="/accounts",method = RequestMethod.GET)
	Collection<Account> readAcounts(Principal principal) {
		this.validateUserAdmin(principal);

		List<Account> accountList = accountRepository
			.findAll().stream()
			.collect(Collectors.toList());

		return  accountList;
	}




	// private void validateUser(Principal principal) {}
	// private void validateUserAdmin(Principal principal) {}


	private void validateUser(Principal principal) {
		if (principal!= null){ 
			System.out.println("VALIDATED");
			String userId = principal.getName();
			this.accountRepository
				.findByUsername(userId)
				.orElseThrow(
					() -> new UserNotFoundException(userId));
		}
	}

	private void validateUserAdmin(Principal principal) {
		if (principal!= null){
			String userId = principal.getName();
			Account acc= this.accountRepository
						.findByUsername(userId).orElseThrow(
							() -> new UserNotFoundException(userId));
						
			Boolean isAdmin= acc.getIsAdmin();
			if (!isAdmin) throw( new AccessDeniedException(userId));
		}
	}
}
// end::code[]
