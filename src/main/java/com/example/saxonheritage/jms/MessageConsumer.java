package com.example.saxonheritage.jms;

import com.example.saxonheritage.config.JmsConfig;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class MessageConsumer {

    private ConnectionFactory connectionFactory;
    private Queue visitorReportsQueue;
    private Queue promotionsQueue;

    public MessageConsumer() {
        this.connectionFactory = JmsConfig.getConnectionFactory();
    }

    public void startListeningToVisitorReports() throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        visitorReportsQueue = session.createQueue("visitorReports");
        MessageConsumer consumer = session.createConsumer(visitorReportsQueue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println("Received visitor report: " + textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        connection.start();
    }

    public void startListeningToPromotions() throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        promotionsQueue = session.createQueue("promotions");
        MessageConsumer consumer;
    }
}