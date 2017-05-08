package de.hska.lkit.demo.web;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RedisMessageReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageReceiver.class);

    private CountDownLatch latch;

    @Autowired
    public RedisMessageReceiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(String message) {
        LOGGER.info("Received <" + message + ">");
        System.out.println("Test");
        latch.countDown();
    }
}

