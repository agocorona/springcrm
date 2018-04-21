
package crmapi;

/**
 * @author Josh Long
 */
@SuppressWarnings("serial")
// tag::code[]
class CustomerNotFoundException extends RuntimeException {

	public CustomerNotFoundException(String customerId) {
		super("could not find customer '" + customerId + "'.");
	}
}
// end::code[]
