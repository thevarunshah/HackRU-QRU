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

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import qru.data.Person;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
public class Utilities {
	
	private static List<Person> database;
	private static Map<String, Person> emailPersonMap = new HashMap<String, Person>();
	
	public static boolean updateEvent(String event, String email){
		Person p = emailPersonMap.get(email);
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
			case "lunch1":
				p.getData().setLunch1(p.getData().getLunch1()+1);
				break;
			case "dinner":
				p.getData().setDinner(p.getData().getDinner()+1);
				break;
			case "midnightSnack":
				p.getData().setMidnightSnack(p.getData().getMidnightSnack()+1);
				break;
			case "breakfast":
				p.getData().setBreakfast(p.getData().getBreakfast()+1);
				break;
			case "lunch2":
				p.getData().setLunch2(p.getData().getLunch2()+1);
				break;
			default:
				return false;
		}
		backupDB();
		return true;
	}
	
	public static Person getPerson(String email){
		return emailPersonMap.get(email);
	}
	
	public static boolean addNewPerson(Person p){
		if(emailPersonMap.containsKey(p.getEmail())){
			return false;
		}
		database.add(p);
		emailPersonMap.put(p.getEmail(), p);
		backupDB();
		return true;
	}
	
	public static List<String> getEmailList(){
		return new ArrayList<String>(emailPersonMap.keySet());
	}
	
	public static void readBackup(){
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("database.ser"))) {
			database = (List<Person>) ois.readObject();
			for(Person p : database){
				emailPersonMap.put(p.getEmail(), p);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			database = new ArrayList<Person>();
		}
	}
	
	public static void readDB(){
		
		String dbUrl = getDbUrl();		
		database = new ArrayList<Person>();
		MongoClient mongo = new MongoClient(new MongoClientURI(dbUrl));
		MongoDatabase mongoDatabase = mongo.getDatabase("hackrusp17");
		MongoCollection<Document> collection = mongoDatabase.getCollection("users");
		for(Document user : collection.find()){
			Document localData = (Document) user.get("local");
			String email = localData.getString("email");
			Document mlhData = (Document) user.get("mlh_data");
			String first = mlhData.getString("first_name");
			String last = mlhData.getString("last_name");
			
			Person p = new Person(email, first, last);
			database.add(p);
			emailPersonMap.put(p.getEmail(), p);
		}
		backupDB();
	}
	
	private static void backupDB(){
		
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("database.ser"))) {
			oos.writeObject(database);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static String getDbUrl(){
		
		//get information stored in config.properties
		Properties prop = new Properties();
		InputStream input = null;
		String dbUrl = null;
		try{
			input = new FileInputStream("config.properties");
			prop.load(input);
			dbUrl = prop.getProperty("dbUrl");
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
		return dbUrl;
	}
}
