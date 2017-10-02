package qru.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
public class PersonData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private boolean checkedIn;
	private boolean tshirt;
	private final Map<String, Integer> events = new HashMap<>();
	
	public boolean isCheckedIn() {
		return checkedIn;
	}
	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}
	public boolean isTshirt() {
		return tshirt;
	}
	public void setTshirt(boolean tshirt) {
		this.tshirt = tshirt;
	}
	public Integer getEventCount(String event) {
		return this.events.get(event);
	}
	public void incrementEventCount(String event) {
		if (this.events.containsKey(event)) {
			this.events.put(event, this.events.get(event)+1);
		}
	}
	public void addNewEvent(String event) {
		if (!this.events.containsKey(event)) {
			this.events.put(event, 0);
		}
	}
	public Map<String, Integer> getEvents() {
		return new HashMap<String, Integer>(this.events);
	}
}
