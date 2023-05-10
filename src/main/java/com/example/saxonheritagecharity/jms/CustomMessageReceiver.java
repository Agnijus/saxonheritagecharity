// Agnijus Botyrius - 21466565
// Distributed Systems (CP60060E) - Assignment
// Project Title - Saxon Heritage Charity Application

package com.example.saxonheritagecharity.jms;

import com.example.saxonheritagecharity.MyMongoClient;
import com.example.saxonheritagecharity.config.JmsConfiguration;
import com.example.saxonheritagecharity.services.GlobalCustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomMessageReceiver {

    private ConnectionFactory jmsConnectionFactory;
    private Queue visitorReportsQueue;
    private Queue promotionsQueue;
    private Queue benefitsQueue;
    private List<String> receivedPromotions;
    private Map<String, List<String>> receivedVisitorReports = new HashMap<>();
    Map<String, Map<String, String>> receivedBenefits;

    @Autowired
    private MyMongoClient myMongoClient;

    // Constructor with injected MongoDB client
    public CustomMessageReceiver(MyMongoClient myMongoClient) {
        this.jmsConnectionFactory = JmsConfiguration.getJmsConnectionFactory();
        this.receivedPromotions = new ArrayList<>();
        this.receivedVisitorReports = new HashMap<>();
    }

    // Returns a copy of the received promotions list
    public List<String> obtainReceivedPromotions() {
        return new ArrayList<>(receivedPromotions);
    }

    // Returns a copy of the received visitor reports for a given member ID
    public List<String> getReceivedVisitorReports(String memberId) {
        List<String> reports = receivedVisitorReports.get(memberId);
        if (reports != null) {
            return new ArrayList<>(reports);
        } else {
            return new ArrayList<>();
        }
    }

    // Initializes the message consumer by starting to listen to different queues
    public void initializeReceiver() {
        try {
            startListeningToVisitorReports();
            startListeningToPromotions();
            startListeningToBenefits();
        } catch (JMSException e) {
            e.printStackTrace();
            System.out.println("Error starting the message consumer.");
        }
    }

    // Starts listening to visitor reports queue
    public void startListeningToVisitorReports() throws JMSException {
        Connection connection = jmsConnectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        visitorReportsQueue = session.createQueue("visitorReports");
        javax.jms.MessageConsumer consumer = session.createConsumer(visitorReportsQueue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String report = textMessage.getText();
                        System.out.println("Received visitor report: " + report);

                        // Extract member ID from the report
                        String[] reportParts = report.split(", ");
                        String memberId = reportParts[0].split(": ")[1];

                        receivedVisitorReports.computeIfAbsent(memberId, k -> new ArrayList<>()).add(report);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        connection.start();
    }

    // Starts listening to promotions queue
    public void startListeningToPromotions() throws JMSException {
        Connection connection = jmsConnectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        promotionsQueue = session.createQueue("promotions");
        javax.jms.MessageConsumer consumer = session.createConsumer(promotionsQueue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof MapMessage) {
                    MapMessage mapMessage = (MapMessage) message;
                    try {
                        String memberId = mapMessage.getString("memberId");
                        String promotion = mapMessage.getString("promotion");
                        System.out.println("Received promotion: Member ID: " + memberId + ", Promotion: " + promotion);

                        myMongoClient.insertPromotion(memberId, promotion);
                    } catch (JMSException | GlobalCustomException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        connection.start();
    }

    // Starts listening to benefits queue
    public void startListeningToBenefits() throws JMSException {
        Connection connection = jmsConnectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        benefitsQueue = session.createQueue("benefits");
        javax.jms.MessageConsumer consumer = session.createConsumer(benefitsQueue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String benefitMessage = textMessage.getText();
                        System.out.println("Received benefit update: " + benefitMessage);

                        // Extract member ID and benefit from the benefit message
                        String[] benefitParts = benefitMessage.split(", ");
                        String memberId = benefitParts[0].split(": ")[1];
                        String benefit = benefitParts[1].split(": ")[1];

                        // Check if the benefit is being added or removed
                        boolean isAdding = true;
                        if (message.propertyExists("isAdding")) {
                            isAdding = message.getBooleanProperty("isAdding");
                        }

                        if (isAdding) {
                            receivedBenefits.computeIfAbsent(memberId, k -> new HashMap<String, String>()).put(benefit, benefit);
                        } else {
                            receivedBenefits.get(memberId).remove(benefit);
                        }
                        System.out.println("Updated benefits map: " + receivedBenefits);

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        connection.start();
    }
}

