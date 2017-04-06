package qru.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import qru.internal.QRGenerator;

/**
 * 
 * @author Varun Shah {@literal varun.shah@rutgers.edu}
 */
@Controller
public class QRUController {
    
    @GetMapping("/viewqr")
    public String generate(@RequestParam(value="email", required=false) String email, Model model) {
    	
    	if(email == null || email.equals("")){
    		model.addAttribute("message", "email is required to generate QR code.");
    		return "error";
    	}
        
    	QRGenerator.generate(email);
    	
        model.addAttribute("email", email);
        return "viewqr";
    }
}
