package com.example.saxonheritage;

import com.example.saxonheritage.jms.CustomMessageConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class AppInitializer {

    @Autowired
    private CustomMessageConsumer customMessageConsumer;

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            customMessageConsumer.init();
            // (rest of the method)
        };
    }
}
