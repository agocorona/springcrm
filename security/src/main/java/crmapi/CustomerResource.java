
package crmapi;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;


class CustomerResource extends ResourceSupport {

	private final Customer customer;

	public CustomerResource(Customer customer) {
		this.customer = customer;
		
	}

	public Customer getCustomer() {
		return customer;
	}
}
// end::code[]
