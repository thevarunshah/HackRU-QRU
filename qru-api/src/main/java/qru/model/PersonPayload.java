package qru.model;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
public class PersonPayload {

	private String email;
	private String firstName;
	private String lastName;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
