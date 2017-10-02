package qru.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import qru.data.Person;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
@Component
public class DBUtilitiesImpl implements DBUtilities {

	protected static String dbUrl;
	protected static String dbName;
	protected static String dbCollectionName;
	
	protected static List<Person> database = new ArrayList<Person>();
	protected static Map<String, Person> emailPersonMap = new HashMap<String, Person>();
	
	@Override
	public void readDB(){
		
		database = new ArrayList<Person>();
		MongoClient mongo = new MongoClient(new MongoClientURI(dbUrl));
		MongoDatabase mongoDatabase = mongo.getDatabase(dbName);
		MongoCollection<Document> collection = mongoDatabase.getCollection(dbCollectionName);
		for(Document user : collection.find()){
			Document mlhData = (Document) user.get("mlh_data");
			String email = mlhData.getString("email");
			String first = mlhData.getString("first_name");
			String last = mlhData.getString("last_name");
			//can fetch more fields here
			
			Person p = new Person(email, first, last);
			database.add(p);
			emailPersonMap.put(p.getEmail(), p);
		}
		mongo.close();
	}
	
	@Override
	public int refreshDB(){
		
		int peopleAdded = 0;
		MongoClient mongo = new MongoClient(new MongoClientURI(dbUrl));
		MongoDatabase mongoDatabase = mongo.getDatabase(dbName);
		MongoCollection<Document> collection = mongoDatabase.getCollection(dbCollectionName);
		for(Document user : collection.find()){
			Document mlhData = (Document) user.get("mlh_data");
			String email = mlhData.getString("email");
			String first = mlhData.getString("first_name");
			String last = mlhData.getString("last_name");
			
			Person p = new Person(email, first, last);
			if(!emailPersonMap.containsKey(p.getEmail())){
				database.add(p);
				emailPersonMap.put(p.getEmail(), p);
				peopleAdded++;
			}
		}
		mongo.close();
		return peopleAdded;
	}
}
