package com.example.saxonheritagecharity.services;

import com.example.saxonheritagecharity.jms.CustomMessageReceiver;
import com.example.saxonheritagecharity.jms.CustomMsgProducer;
import com.example.saxonheritagecharity.model.Member;
import com.example.saxonheritagecharity.MyMongoClient;

import org.bson.Document;
import org.bson.types.ObjectId;

import javax.jws.WebService;
import java.util.List;

// This annotation tells the framework that this is a web service and specifies the interface to use for requests.
@WebService(endpointInterface = "com.example.saxonheritagecharity.services.MemberService")
public class MemberServiceImplementation implements MemberService {

    private MyMongoClient myMongoClient;
    private CustomMsgProducer customMsgProducer;
    private CustomMessageReceiver customMessageReceiver;

    // Constructor that creates a new instance of this class and initializes the MongoDB client and JMS producer and receiver.
    public MemberServiceImplementation(MyMongoClient myMongoClient) {
        this.myMongoClient = myMongoClient;
        this.customMsgProducer = new CustomMsgProducer();
        this.customMessageReceiver = new CustomMessageReceiver(myMongoClient);
    }

    // This method registers a new member with the given name and email address and returns the member's ID.
    @Override
    public String registerMember(String name, String email) {
        // Generate a new member ID.
        ObjectId memberId = new ObjectId();
        // Create a new Member object with the given name, email, and ID.
        Member newMember = new Member(memberId, name, email);
        // Create a new MongoDB document to store the member's information.
        Document memberDoc = new Document()
                .append("_id", newMember.getId())
                .append("name", newMember.getName())
                .append("email", newMember.getEmail());
        // Insert the document into the MongoDB collection for members.
        myMongoClient.insertDocument(memberDoc, "memberAccount");
        // Print the document to the console for debugging purposes.
        System.out.println(memberDoc);
        // Return a success message with the new member's ID.
        return "Member registration successful. Member ID: " + newMember.getId().toHexString();
    }

    // This method updates the email address for a member with the given ID.
    @Override
    public String updateMemberDetails(String memberId, String email) {
        // Find the existing member document in the MongoDB collection for members.
        Document existingMember = myMongoClient.findDocumentById(memberId, "memberAccount");

        // If the member exists, update their email address in the document.
        if (existingMember != null) {
            myMongoClient.updateDocumentById(memberId, "email", email, "memberAccount");
            return "Member details updated successfully";
        } else {
            // If the member does not exist, return an error message.
            return "Member not found";
        }
    }

    // This method retrieves the membership information for a member with the given ID.
    @Override
    public String getMembershipInfo(String memberId) {
        // Find the existing member document in the MongoDB collection for members.
        Document existingMember = myMongoClient.findDocumentById(memberId, "memberAccount");

        // If the member exists, return their name, email, and ID as a string.
        if (existingMember != null) {
            // Get the member's ID as a hexadecimal string.
            String memberIdHexString = existingMember.getObjectId("_id").toHexString();

            // Get the member's name and email.
            String memberName = existingMember.getString("name");
            String memberEmail = existingMember.getString("email");

            // Return a string with the member's ID, name, and email.
            return "Member ID: " + memberIdHexString + ", Name: " + memberName + ", Email: " + memberEmail;
        } else {
            // If the member does not exist, return an error message.
            return "Member not found";
        }
    }
    // This method submits a visitor report for a member with the given ID and a list of visitor reports.
    // The report is sent to a JMS topic and saved to the MongoDB collection for visitor reports.
    @Override
    public void submitVisitorReport(String memberId, List<String> visitorReports) throws GlobalCustomException {
    // Create a message with the member ID and visitor reports.
        String reportMessage = "Member ID: " + memberId + ", Report: " + String.join(", ", visitorReports);
        try {
    // Send the message to the JMS topic using the producer.
            customMsgProducer.dispatchVisitorReport(reportMessage);
    // Save the visitor reports to the MongoDB collection for visitor reports using the MongoDB client.
            myMongoClient.insertVisitorReports(memberId, visitorReports);
        } catch (GlobalCustomException e) {
    // If there is a GlobalCustomException, re-throw it.
            throw e;
        } catch (Exception e) {
    // If there is an unexpected error, print the stack trace and throw a new GlobalCustomException with a 500 status code.
            e.printStackTrace();
            throw new GlobalCustomException("Error submitting visitor reports.", 500);
        }
    }
    // This method sends a promotion update to a member with the given ID.
// The promotion update is sent to a JMS queue and saved to the MongoDB collection for member promotions.
    @Override
    public void sendPromotion(String memberId, String promotion) throws GlobalCustomException {
        try {
            // Send the promotion update to the JMS queue using the producer.
            customMsgProducer.dispatchPromotionUpdate(memberId, promotion);
            // Save the promotion update to the MongoDB collection for member promotions using the MongoDB client.
            myMongoClient.insertPromotion(memberId, promotion);
        } catch (GlobalCustomException e) {
            // If there is a GlobalCustomException, re-throw it.
            throw e;
        } catch (Exception e) {
            // If there is an unexpected error, throw a new GlobalCustomException with a 2 status code.
            throw new GlobalCustomException("Error sending promotion.", 2);
        }
    }

    // This method retrieves a list of visitor reports for a member with the given ID.
    @Override
    public List<String> getVisitorReports(String memberId) throws GlobalCustomException {
        // Find the existing member document in the MongoDB collection for members.
        Document member = myMongoClient.findDocumentById(memberId, "memberAccount");

        // If the member does not exist, throw a GlobalCustomException with a 404 status code.
        if (member == null) {
            throw new GlobalCustomException("Member not found.", 404);
        }

        // Find the existing visitor reports document in the MongoDB collection for visitor reports.
        Document visitorReportsDocument = myMongoClient.findDocumentById(memberId, "visitorReports");

        // If the visitor reports document exists, get the list of visitor reports and return it.
        if (visitorReportsDocument != null) {
            List<String> visitorReports = visitorReportsDocument.getList("visitorReports", String.class);

            // If the list of visitor reports is empty, throw a GlobalCustomException with a 404 status code.
            if (visitorReports == null || visitorReports.isEmpty()) {
                throw new GlobalCustomException("Member has no visitor reports.", 404);
            } else {
                return visitorReports;
            }
        } else {
            // If the visitor reports document does not exist, throw a GlobalCustomException with a 404 status code.
            throw new GlobalCustomException("Member has no visitor reports.", 404);
        }
    }

    // This method retrieves a list of promotions for a member with the given
    // If the member does not exist, an exception is thrown with a 404 status code.
    @Override
    public List<String> getPromotions(String memberId) throws GlobalCustomException {
    // Find the existing member document in the MongoDB collection for members.
        Document member = myMongoClient.findDocumentById(memberId, "memberAccount");
        // If the member does not exist, throw an exception with a 404 status code.
        if (member == null) {
            throw new GlobalCustomException("Member not found.", 404);
        }

        // Find the member promotions document in the MongoDB collection for member promotions.
        Document memberPromotionsDocument = myMongoClient.findDocumentById(memberId, "memberPromotions");

        // If the member promotions document exists, retrieve the promotions list.
        if (memberPromotionsDocument != null) {
            List<String> memberPromotions = memberPromotionsDocument.get("promotions", List.class);

            // If the promotions list is not empty, return it.
            if (memberPromotions != null && !memberPromotions.isEmpty()) {
                return memberPromotions;
            }
        }

        // If no promotions were found, throw an exception with a 404 status code.
        throw new GlobalCustomException("Member has no promotions.", 404);
        }
}


