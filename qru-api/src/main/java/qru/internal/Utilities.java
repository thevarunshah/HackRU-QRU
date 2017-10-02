package qru.internal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import qru.data.Person;
import qru.data.PersonData;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
@Component
public class Utilities {

	public boolean restartingServer = false;
	protected List<Person> localDatabase;
	protected Map<String, Person> emailPersonMap = new HashMap<String, Person>();
	private String[] eventsList;
	
	public boolean updateEvent(String event, String email){
		Person p = emailPersonMap.get(email);
		if(p == null){
			return false;
		}
		switch(event){
			case "checkIn":
				if(p.getData().isCheckedIn()){
					return false;
				}
				p.getData().setCheckedIn(true);
				break;
			case "tshirt":
				if(p.getData().isTshirt()){
					return false;
				}
				p.getData().setTshirt(true);
				break;
			default:
				if(!p.getData().getEvents().containsKey(event)) {
					return false;
				}
				p.getData().incrementEventCount(event);
				break;
		}
		backupDB();
		return true;
	}
	
	public Person getPerson(String email){
		return emailPersonMap.get(email);
	}
	
	public boolean addNewPerson(Person p){
		if(emailPersonMap.containsKey(p.getEmail())){
			return false;
		}
		for(String event : eventsList) {
			p.getData().addNewEvent(event);
		}
		localDatabase.add(p);
		emailPersonMap.put(p.getEmail(), p);
		backupDB();
		return true;
	}
	
	public List<String> getEmailList(){
		return new ArrayList<String>(emailPersonMap.keySet());
	}
	
	public void readBackup(){
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("database.ser"))) {
			localDatabase = (List<Person>) ois.readObject();
			for(Person p : localDatabase){
				emailPersonMap.put(p.getEmail(), p);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			localDatabase = new ArrayList<Person>();
		}
	}
	
	public JSONObject getStats(){
		
		int checkedIn = 0, tshirt = 0;
		int[] eventCounts = new int[eventsList.length];
		int[] eventCountsPlus = new int[eventsList.length];
		for(Person p : localDatabase){
			PersonData data = p.getData();
			if(data.isCheckedIn()){
				checkedIn++;
			}
			if(data.isTshirt()){
				tshirt++;
			}
			for(int i = 0; i < eventsList.length; i++) {
				if(!p.getData().getEvents().containsKey(eventsList[i])) {
					continue; //this should never happen
				}
				if(p.getData().getEventCount(eventsList[i]) > 0) {
					eventCounts[i]++;
					if(p.getData().getEventCount(eventsList[i]) > 1) {
						eventCountsPlus[i]++;
					}
				}
			}
		}
		
		JSONObject stats = new JSONObject();
		stats.put("checked-in", checkedIn);
		stats.put("t-shirt", tshirt);
		for(int i = 0; i < eventsList.length; i++) {
			stats.put(eventsList[i], eventCounts[i]);
			stats.put(eventsList[i]+" (more than once)", eventCountsPlus[i]);
		}
		return stats;
	}
	
	protected void backupDB(){
		
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("local_database.ser"))) {
			oos.writeObject(localDatabase);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void readProperties(){
		
		//get information stored in config.properties
		Properties prop = new Properties();
		InputStream input = null;
		try{
			input = new FileInputStream("config.properties");
			prop.load(input);
			restartingServer = Boolean.parseBoolean(prop.getProperty("restartingServer"));
			eventsList = prop.getProperty("events").split(",");
			//db properties
			if(prop.getProperty("useExternalDB").equals("true")) {
				DBUtilitiesImpl.dbUrl = prop.getProperty("dbUrl");
				DBUtilitiesImpl.dbName = prop.getProperty("dbName");
				DBUtilitiesImpl.dbCollectionName = prop.getProperty("dbCollectionName");
			}
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			if (input != null){
				try{
					input.close();
				} catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}
}
