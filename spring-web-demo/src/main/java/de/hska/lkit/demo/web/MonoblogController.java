package de.hska.lkit.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class MonoblogController {

    private final Command handler;
    
    @Autowired
    public MonoblogController(Command handler) {
        super();
        this.handler = handler;
    }
	
	@RequestMapping(value = "")
	public String index(@ModelAttribute Monoblog monoblog, Model model) {
		return "monoblog";
	}
	
	@MessageMapping("/command")
	@SendToUser("/queue/replies")
	public Result commandRequest(Request request) {
		return handler.process(request);
	}
	

}

