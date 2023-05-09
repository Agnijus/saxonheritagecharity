package com.example.saxonheritage.config;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsConfig {

    private static final String ACTIVE_MQ_URL = "tcp://localhost:61616";
    private static ConnectionFactory connectionFactory;

    public static ConnectionFactory getConnectionFactory() {
        if (connectionFactory == null) {
            connectionFactory = new ActiveMQConnectionFactory(ACTIVE_MQ_URL);
        }
        return connectionFactory;
    }
}
