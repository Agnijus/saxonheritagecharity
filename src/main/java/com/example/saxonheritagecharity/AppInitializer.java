package com.example.saxonheritagecharity;

import com.example.saxonheritagecharity.jms.CustomMessageReceiver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class AppInitializer {

    // Autowired annotation is used to inject the CustomMessageConsumer instance into this class.
    @Autowired
    private CustomMessageReceiver customMessageReceiver;

    // This method returns a command line runner that will run the application when it starts up.
//    @Bean
//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//        // The run() method of CustomMessageConsumer is called to initialize the JMS listener.
//        // The JMS listener will listen for incoming messages and handle them appropriately.
//        return args -> {
//            customMessageReceiver.init();
//        };
//    }
}