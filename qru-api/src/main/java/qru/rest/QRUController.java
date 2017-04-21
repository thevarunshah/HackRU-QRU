package qru.rest;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import qru.data.Person;
import qru.internal.Utilities;
import qru.model.PersonPayload;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
@RestController
public class QRUController {

	private static final boolean restartingServer = true; 

	@PostConstruct
	public void init(){
		if(restartingServer){
			Utilities.readBackup();
		} else{
			Utilities.readDB();
		}
	}

	@RequestMapping(path="/info/{email:.+}", method=RequestMethod.GET)
	public ResponseEntity<Person> getPerson(@PathVariable String email){
		Person p = Utilities.getPerson(email);
		if(p == null){
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(p);
	}

	@RequestMapping(path="/update/{email:.+}/{event}", method=RequestMethod.POST)
	public ResponseEntity<Person> updateEvent(@PathVariable String event, @PathVariable String email){
		if(!Utilities.updateEvent(event, email)){
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(Utilities.getPerson(email));
	}

	@RequestMapping(path="/add", method=RequestMethod.POST, consumes="application/json")
	public ResponseEntity<Person> addPerson(@RequestBody PersonPayload payload) {
		Person p = new Person(payload.getEmail(), payload.getFirstName(), payload.getLastName());
		if(!Utilities.addNewPerson(p)){
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(p);
	}

	@RequestMapping(path="/emaillist", method=RequestMethod.GET)
	public ResponseEntity<List<String>> emailList(){
		return ResponseEntity.ok(Utilities.getEmailList());
	}
}
