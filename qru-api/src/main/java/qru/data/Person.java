package qru.data;

import java.io.Serializable;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
public class Person implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private final String email;
	private final String firstName;
	private final String lastName;
	private PersonData data;
	
	public Person(String email, String first, String last){
		this.email = email;
		this.firstName = first;
		this.lastName = last;
		this.data = new PersonData();
	}
	
	public String getEmail() {
		return email;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public PersonData getData(){
		return data;
	}
}
