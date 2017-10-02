package qru.rest;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qru.data.Person;
import qru.internal.DBUtilitiesImpl;
import qru.internal.Utilities;
import qru.model.PersonPayload;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
@RestController
public class QRUController {

	@Autowired
	private Utilities utilities;
	
	@Autowired
	private DBUtilitiesImpl dbUtlities;

	@PostConstruct
	public void init(){
		utilities.readProperties();
		if(utilities.restartingServer){
			utilities.readBackup();
		} else{
			dbUtlities.readDB();
		}
	}

	@RequestMapping(path="/info/{email:.+}", method=RequestMethod.GET)
	public ResponseEntity<Person> getPerson(@PathVariable String email){
		Person person = utilities.getPerson(email);
		return person == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(person);
	}

	@RequestMapping(path="/update/{email:.+}/{event}", method=RequestMethod.POST)
	public ResponseEntity<Person> updateEvent(@PathVariable String event, @PathVariable String email){
		return utilities.updateEvent(event, email) ? ResponseEntity.ok(utilities.getPerson(email)) : ResponseEntity.badRequest().build();
	}

	@RequestMapping(path="/add", method=RequestMethod.POST, consumes="application/json")
	public ResponseEntity<Person> addPerson(@RequestBody PersonPayload payload) {
		Person person = new Person(payload.getEmail(), payload.getFirstName(), payload.getLastName());
		return utilities.addNewPerson(person) ? ResponseEntity.status(HttpStatus.CREATED).body(person) : ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

	@RequestMapping(path="/update", method=RequestMethod.POST)
	public ResponseEntity<Integer> refreshDB() {
		return ResponseEntity.ok(dbUtlities.refreshDB());
	}
	
	@RequestMapping(path="/stats", method=RequestMethod.GET)
	public ResponseEntity<String> getStats(){
		return ResponseEntity.ok(utilities.getStats().toString());
	}

	@RequestMapping(path="/emaillist", method=RequestMethod.GET)
	public ResponseEntity<List<String>> emailList(){
		return ResponseEntity.ok(utilities.getEmailList());
	}
}
