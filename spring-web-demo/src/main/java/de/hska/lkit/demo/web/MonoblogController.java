package de.hska.lkit.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

@Controller
public class MonoblogController {

	@RequestMapping(value = "")
	public String test(@ModelAttribute Monoblog monoblog, Model model) {
	
	return "monoblog";
	}
	
	
	 @MessageMapping("/hello")
	 @SendTo("/topic/greetings")
	 public Greeting greeting(HelloMessage message) throws Exception {
	    Thread.sleep(1000); // simulated delay
	    return new Greeting("Hello, " + message.getName() + "!");
	 }
	

}
