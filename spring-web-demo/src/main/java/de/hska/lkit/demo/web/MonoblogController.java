package de.hska.lkit.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;

@Controller
public class MonoblogController {

	@RequestMapping(value = "")
	public String test(@ModelAttribute Monoblog monoblog, Model model) {
		return "monoblog";
	}
	
	
	 @MessageMapping("/command")
	 @SendToUser("/queue/replies")
	 public Greeting greeting(HelloMessage message) throws Exception {
		System.out.println("Received request!");
	    Thread.sleep(1000); // simulated delay
	    return new Greeting("Hello, " + message.getName() + "!");
	 }
	

}
