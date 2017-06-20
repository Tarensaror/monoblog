package de.hska.lkit.demo.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import de.hska.lkit.demo.web.redis.queue.MessageCreateQueue;

@SpringBootApplication
public class SpringWebDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringWebDemoApplication.class, args);
	}
}
