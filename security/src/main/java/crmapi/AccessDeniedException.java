
package crmapi;

/**
 * @author Josh Long
 */
@SuppressWarnings("serial")
// tag::code[]
class AccessDeniedException extends RuntimeException {

	public AccessDeniedException(String userId) {
		super("Access denied to user '" + userId + "'.");
	}
}
// end::code[]
