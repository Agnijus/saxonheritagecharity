package com.example.saxonheritage.jms;

import com.example.saxonheritage.config.JmsConfig;

import javax.jms.*;

public class CustomMessageProducer {

    private ConnectionFactory connectionFactory;
    private Queue visitorReportsQueue;
    private Queue promotionsQueue;
    private Queue benefitsQueue;

    public CustomMessageProducer() {
        this.connectionFactory = JmsConfig.getConnectionFactory();
    }

    public void sendVisitorReport(String report) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        visitorReportsQueue = session.createQueue("visitorReports");
        MessageProducer producer = session.createProducer(visitorReportsQueue);
        TextMessage message = session.createTextMessage(report);

        producer.send(message);

        producer.close();
        session.close();
        connection.close();
    }

    public void sendPromotionUpdate(String memberId, String promotion) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        promotionsQueue = session.createQueue("promotions");
        MessageProducer producer = session.createProducer(promotionsQueue);

        MapMessage message = session.createMapMessage();
        message.setString("memberId", memberId);
        message.setString("promotion", promotion);

        producer.send(message);

        producer.close();
        session.close();
        connection.close();
    }


    public void sendBenefitUpdate(String benefitMessage, boolean isAdding) throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        benefitsQueue = session.createQueue("benefits");
        javax.jms.MessageProducer producer = session.createProducer(benefitsQueue);

        TextMessage message = session.createTextMessage();
        message.setText(benefitMessage);
        message.setBooleanProperty("isAdding", isAdding);

        producer.send(message);

        producer.close();
        session.close();
        connection.close();
    }


}
