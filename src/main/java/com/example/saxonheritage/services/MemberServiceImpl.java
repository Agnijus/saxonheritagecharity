package com.example.saxonheritage.services;

import com.example.saxonheritage.SaxonHeritageApp;
import com.example.saxonheritage.jms.CustomMessageConsumer;
import com.example.saxonheritage.jms.CustomMessageProducer;
import com.example.saxonheritage.model.Member;
import com.example.saxonheritage.MyMongoClient;
import com.google.gson.JsonSyntaxException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;

import javax.jws.WebService;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@WebService(endpointInterface = "com.example.saxonheritage.services.MemberService")
public class MemberServiceImpl implements MemberService {

    private MyMongoClient myMongoClient;
    private CustomMessageProducer customMessageProducer;
    private CustomMessageConsumer customMessageConsumer;


    public MemberServiceImpl(MyMongoClient myMongoClient) {
        this.myMongoClient = myMongoClient;
        this.customMessageProducer = new CustomMessageProducer();
        this.customMessageConsumer = new CustomMessageConsumer(myMongoClient);
    }



    @Override
    public String registerMember(String name, String email) {
        ObjectId memberId = new ObjectId();
        Member newMember = new Member(memberId, name, email);
        Document memberDoc = new Document()
                .append("_id", newMember.getId())
                .append("name", newMember.getName())
                .append("email", newMember.getEmail());
        myMongoClient.insertDocument(memberDoc, "memberService");
        System.out.println(memberDoc);
        return "Member registration successful. Member ID: " + newMember.getId().toHexString();
    }




    @Override
    public String updateMemberDetails(String memberId, String email) {
        Document existingMember = myMongoClient.findDocumentById(memberId, "memberService");

        if (existingMember != null) {
            myMongoClient.updateDocumentById(memberId, "email", email, "memberService");
            return "Member details updated successfully";
        } else {
            return "Member not found";
        }
    }

    @Override
    public String getMembershipInfo(String memberId) {
        Document existingMember = myMongoClient.findDocumentById(memberId, "memberService");

        if (existingMember != null) {
            return "Member ID: " + existingMember.getObjectId("_id").toHexString() + ", Name: " + existingMember.getString("name") + ", Email: " + existingMember.getString("email");
        } else {
            return "Member not found";
        }
    }

    @Override
    public void submitVisitorReport(String memberId, List<String> visitorReports) throws GlobalCustomException {
        String reportMessage = "Member ID: " + memberId + ", Report: " + String.join(", ", visitorReports);
        try {
            customMessageProducer.sendVisitorReport(reportMessage); // Send to JMS
            myMongoClient.insertVisitorReports(memberId, visitorReports); // Save to MongoDB
        } catch (GlobalCustomException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalCustomException("Error submitting visitor reports.", 500);
        }
    }

    @Override
    public void sendPromotion(String memberId, String promotion) throws GlobalCustomException {
        try {
            customMessageProducer.sendPromotionUpdate(memberId, promotion); // Send to JMS
            myMongoClient.insertPromotion(memberId, promotion); // Save to MongoDB
        } catch (GlobalCustomException e) {
            // Handle the exception, for example, rethrow it or log it.
            throw e;
        } catch (Exception e) {
            throw new GlobalCustomException("Error sending promotion.", 2);
        }
    }





    @Override
    public List<String> getVisitorReports(String memberId) throws GlobalCustomException {
        Document member = myMongoClient.findDocumentById(memberId, "memberService");
        if (member == null) {
            throw new GlobalCustomException("Member not found.", 404);
        }
        System.out.println("Found member: " + member.toJson()); // Debug log

        Document visitorReportsDocument = myMongoClient.findDocumentById(memberId, "visitorReports");
        if (visitorReportsDocument != null) {
            List<String> visitorReports = visitorReportsDocument.getList("visitorReports", String.class);
            if (visitorReports == null || visitorReports.isEmpty()) {
                throw new GlobalCustomException("Member has no visitor reports.", 404);
            } else {
                return visitorReports;
            }
        } else {
            throw new GlobalCustomException("Member has no visitor reports.", 404);
        }
    }







    @Override
    public List<String> getPromotions(String memberId) throws GlobalCustomException {
        Document member = myMongoClient.findDocumentById(memberId, "memberService");
        if (member == null) {
            throw new GlobalCustomException("Member not found.", 404);
        }
        System.out.println("Found member: " + member.toJson()); // Debug log

        Document memberPromotionsDocument = myMongoClient.findDocumentById(memberId, "memberPromotions");
        if (memberPromotionsDocument != null) {
            List<String> memberPromotions = memberPromotionsDocument.get("promotions", List.class);
            if (memberPromotions == null || memberPromotions.isEmpty()) {
                throw new GlobalCustomException("Member has no promotions.", 404);
            } else {
                return memberPromotions;
            }
        } else {
            throw new GlobalCustomException("Member has no promotions.", 404);
        }
    }

}