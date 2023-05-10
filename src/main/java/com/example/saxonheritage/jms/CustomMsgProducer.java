package com.example.saxonheritage.jms;

import com.example.saxonheritage.config.JmsConfiguration;

import javax.jms.*;

public class CustomMsgProducer {

    private ConnectionFactory jmsConnectionFactory;
    private Queue visitorReportsQ;
    private Queue promotionsQ;
    private Queue benefitsQ;

    public CustomMsgProducer() {
        this.jmsConnectionFactory = JmsConfiguration.getJmsConnectionFactory();
    }

    // Sends a visitor report to the visitorReports queue
    public void dispatchVisitorReport(String report) throws JMSException {
        Connection connection = jmsConnectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        visitorReportsQ = session.createQueue("visitorReports");
        MessageProducer producer = session.createProducer(visitorReportsQ);
        TextMessage message = session.createTextMessage(report);

        producer.send(message);

        producer.close();
        session.close();
        connection.close();
    }

    // Sends a promotion update to the promotions queue
    public void dispatchPromotionUpdate(String memberId, String promotion) throws JMSException {
        Connection connection = jmsConnectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        promotionsQ = session.createQueue("promotions");
        MessageProducer producer = session.createProducer(promotionsQ);

        MapMessage message = session.createMapMessage();
        message.setString("memberId", memberId);
        message.setString("promotion", promotion);

        producer.send(message);

        producer.close();
        session.close();
        connection.close();
    }

    // Sends a benefit update to the benefits queue
    public void dispatchBenefitUpdate(String benefitMessage, boolean isAdding) throws JMSException {
        Connection connection = jmsConnectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        benefitsQ = session.createQueue("benefits");
        javax.jms.MessageProducer producer = session.createProducer(benefitsQ);

        TextMessage message = session.createTextMessage();
        message.setText(benefitMessage);
        message.setBooleanProperty("isAdding", isAdding);

        producer.send(message);

        producer.close();
        session.close();
        connection.close();
    }
}
