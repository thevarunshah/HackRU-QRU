package qru.data;

import java.io.Serializable;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
public class PersonData implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private boolean checkedIn;
	private boolean tshirt;
	private int lunch1;
	private int dinner;
	private int midnightSnack;
	private int breakfast;
	private int lunch2;
	
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
	public int getLunch1() {
		return lunch1;
	}
	public void setLunch1(int lunch1) {
		this.lunch1 = lunch1;
	}
	public int getDinner() {
		return dinner;
	}
	public void setDinner(int dinner) {
		this.dinner = dinner;
	}
	public int getMidnightSnack() {
		return midnightSnack;
	}
	public void setMidnightSnack(int midnightSnack) {
		this.midnightSnack = midnightSnack;
	}
	public int getBreakfast() {
		return breakfast;
	}
	public void setBreakfast(int breakfast) {
		this.breakfast = breakfast;
	}
	public int getLunch2() {
		return lunch2;
	}
	public void setLunch2(int lunch2) {
		this.lunch2 = lunch2;
	}
}
