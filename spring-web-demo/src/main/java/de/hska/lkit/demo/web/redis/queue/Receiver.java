package de.hska.lkit.demo.web.redis.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class Receiver {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	public void receive(String authorName) {		
		System.out.println("received: " + authorName);
		messagingTemplate.convertAndSend("/topic/postannounce/" + authorName, authorName);
	}
	
}