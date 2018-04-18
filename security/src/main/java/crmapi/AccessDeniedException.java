
package crmapi;

/**
 * @author Josh Long
 */
@SuppressWarnings("serial")
// tag::code[]
class AccessDeniedException extends RuntimeException {

	public AccessDeniedException(String userId) {
		super("could not find user '" + userId + "'.");
	}
}
// end::code[]
