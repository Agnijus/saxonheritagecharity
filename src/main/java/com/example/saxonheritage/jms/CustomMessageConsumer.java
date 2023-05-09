package com.example.saxonheritage.jms;

import com.example.saxonheritage.MyMongoClient;
import com.example.saxonheritage.config.JmsConfig;
import com.example.saxonheritage.services.GlobalCustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CustomMessageConsumer {

    private ConnectionFactory connectionFactory;
    private Queue visitorReportsQueue;
    private Queue promotionsQueue;
    private Queue benefitsQueue;
    private List<String> receivedPromotions;
    private Map<String, List<String>> receivedVisitorReports = new HashMap<>();
    private Map<Integer, Map<String, String>> receivedBenefits = new ConcurrentHashMap<>();

    @Autowired
    private MyMongoClient myMongoClient;


    public CustomMessageConsumer(MyMongoClient myMongoClient) {
        this.connectionFactory = JmsConfig.getConnectionFactory();
        this.receivedPromotions = new ArrayList<>();
        this.receivedVisitorReports = new HashMap<>();
    }



    public List<String> getReceivedPromotions() {
        return new ArrayList<>(receivedPromotions);
    }

    public List<String> getReceivedVisitorReports(String memberId) {
        List<String> reports = receivedVisitorReports.get(memberId);
        if (reports != null) {
            return new ArrayList<>(reports);
        } else {
            return new ArrayList<>();
        }
    }

    public void init() {
        try {
            startListeningToVisitorReports();
            startListeningToPromotions();
            startListeningToBenefits();
        } catch (JMSException e) {
            e.printStackTrace();
            System.out.println("Error starting the message consumer.");
        }
    }


    public void startListeningToVisitorReports() throws JMSException {
        Connection connection = connectionFactory.createConnection();
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



    public void startListeningToPromotions() throws JMSException {
        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        promotionsQueue = session.createQueue("promotions");
        javax.jms.MessageConsumer consumer = session.createConsumer(promotionsQueue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String promotionText = textMessage.getText();
                        System.out.println("Received promotion: " + promotionText);

                        // Extract memberId and promotion from the received message
                        String[] parts = promotionText.split(", ");
                        String memberIdPart = parts[0].replace("Member ID: ", "");
                        String promotionPart = parts[1].replace("Promotion: ", "");

                        myMongoClient.insertPromotion(memberIdPart, promotionPart);
                    } catch (JMSException | GlobalCustomException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        connection.start();
    }

    public void startListeningToBenefits() throws JMSException {
        Connection connection = connectionFactory.createConnection();
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
                        int memberId = Integer.parseInt(benefitParts[0].split(": ")[1]);
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
