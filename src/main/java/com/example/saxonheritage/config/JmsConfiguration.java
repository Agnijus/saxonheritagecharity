// JMS configuration class for Saxon Heritage
package com.example.saxonheritage.config;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JmsConfiguration {

    // ActiveMQ URL for the JMS broker
    private static final String ACTIVE_MQ_BROKER_URL = "tcp://localhost:61616";
    // Connection factory to create JMS connections
    private static ConnectionFactory jmsConnectionFactory;

    // Returns a singleton instance of the JMS connection factory
    public static ConnectionFactory getJmsConnectionFactory() {
        if (jmsConnectionFactory == null) {
            jmsConnectionFactory = new ActiveMQConnectionFactory(ACTIVE_MQ_BROKER_URL);
        }
        return jmsConnectionFactory;
    }
}