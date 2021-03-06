package de.hska.lkit.demo.web.redis.queue;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageCreateQueue {
	
	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, 
			MessageListenerAdapter listenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("createmessage"));
		return container;
	}
	
	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receive");
	}
	
	@Bean
	Receiver receiver() {
		return new Receiver();
	}
	
	
}