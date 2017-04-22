package qru.internal;

import static com.mongodb.client.model.Filters.eq;

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
	
	private List<Person> database;
	private Map<String, Person> emailPersonMap = new HashMap<String, Person>();
	private String dbUrl;
	
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
				updateDB(email);
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
	
	public Person getPerson(String email){
		return emailPersonMap.get(email);
	}
	
	public boolean addNewPerson(Person p){
		if(emailPersonMap.containsKey(p.getEmail())){
			return false;
		}
		database.add(p);
		emailPersonMap.put(p.getEmail(), p);
		backupDB();
		return true;
	}
	
	public List<String> getEmailList(){
		return new ArrayList<String>(emailPersonMap.keySet());
	}
	
	public void readBackup(){
		
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
	
	public void readDB(){
		
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
		mongo.close();
		backupDB();
	}
	
	private void updateDB(String email){
		
		try{
			MongoClient mongo = new MongoClient(new MongoClientURI(dbUrl));
			MongoDatabase mongoDatabase = mongo.getDatabase("hackrusp17");
			MongoCollection<Document> collection = mongoDatabase.getCollection("users");
			collection.updateOne(eq("local.email", email), 
					new Document("$set", new Document("registration_status", 5)));
			mongo.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void backupDB(){
		
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("database.ser"))) {
			oos.writeObject(database);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void readDbUrl(){
		
		//get information stored in config.properties
		Properties prop = new Properties();
		InputStream input = null;
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
	}
}
