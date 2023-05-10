// Agnijus Botyrius - 21466565
// Distributed Systems (CP60060E) - Assignment
// Project Title - Saxon Heritage Charity Application

package com.example.saxonheritagecharity.services;

import com.example.saxonheritagecharity.jms.CustomMsgProducer;
import com.example.saxonheritagecharity.jms.CustomMessageReceiver;
import com.example.saxonheritagecharity.MyMongoClient;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BenefitsServiceImplementation implements BenefitsService {
    private CustomMsgProducer customMsgProducer;
    private CustomMessageReceiver customMessageReceiver;
    private MyMongoClient myMongoClient;

    // Constructor
    @Autowired
    public BenefitsServiceImplementation(MyMongoClient myMongoClient) {
        this.myMongoClient = myMongoClient;
        customMsgProducer = new CustomMsgProducer();
        customMessageReceiver = new CustomMessageReceiver(myMongoClient);
    }

    // Add a benefit to the member
    @Override
    public void addMemberBenefit(String memberId, String benefit) throws GlobalCustomException {
        try {
            // Convert memberId to ObjectId and find the member document in the database
            ObjectId memberIdObj = new ObjectId(memberId);
            Document memberDoc = myMongoClient.findDocumentById(memberId, "memberAccount");

            // Check if the member exists
            if (memberDoc != null) {
                // Find existing benefits for the member
                Document existingBenefit = myMongoClient.findDocumentById(memberId, "memberBenefits");

                // Check if the member has existing benefits
                if (existingBenefit != null) {
                    // Get the list of existing benefits
                    List<String> existingBenefits = existingBenefit.getList("benefit", String.class);

                    // Add the benefit to the list and update the database
                    existingBenefits.add(benefit);
                    myMongoClient.updateDocumentById(memberId, "benefit", existingBenefits, "memberBenefits");
                } else {
                    // If no existing benefits found, create a new benefits document for the member
                    List<String> benefitsList = new ArrayList<>();
                    benefitsList.add(benefit);
                    Document newBenefit = new Document("_id", memberIdObj)
                            .append("benefit", benefitsList);
                    myMongoClient.insertDocument(newBenefit, "memberBenefits");
                }

                // Send a message to notify about the added benefit
                String benefitMessage = "Member ID: " + memberId + ", Benefit: " + benefit;
                customMsgProducer.dispatchBenefitUpdate(benefitMessage, true);
            } else {
                throw new GlobalCustomException("Member not found with ID: " + memberId, 404);
            }
        } catch (IllegalArgumentException | JMSException e) {
            throw new GlobalCustomException("Error while adding the benefit to the member: " + e.getMessage(), 500);
        }
    }

    // Remove a benefit from a member
    @Override
    public void removeMemberBenefit(String memberId, String benefit) throws GlobalCustomException {
        try {
            // Find the member in the database
            ObjectId memberIdObj = new ObjectId(memberId);
            Document memberDoc = myMongoClient.findDocumentById(memberId, "memberAccount");

            // Check if the member exists
            if (memberDoc != null) {
                // Find existing benefits for the member
                Document existingBenefit = myMongoClient.findDocumentById(memberId, "memberBenefits");

                // Check if the member has existing benefits
                if (existingBenefit != null) {
                    // Get the list of existing benefits
                    List<String> existingBenefits = existingBenefit.getList("benefit", String.class);

                    // Check if the benefit is in the list
                    if (existingBenefits != null && existingBenefits.contains(benefit)) {
                        // Remove the benefit from the list and update the database
                        existingBenefits.remove(benefit);
                        myMongoClient.updateDocumentById(memberId, "benefit", existingBenefits, "memberBenefits");

                        // Send a message to notify about the removed benefit
                        String benefitMessage = "Member ID: " + memberId + ", Benefit: " + benefit;
                        customMsgProducer.dispatchBenefitUpdate(benefitMessage, false);
                    } else {
                        throw new GlobalCustomException("Benefit not found for the member.", 404);
                    }
                } else {
                    throw new GlobalCustomException("No benefits found for the member with ID: " + memberId, 404);
                }
            } else {
                throw new GlobalCustomException("Member not found with ID: " + memberId, 404);
            }
        } catch (IllegalArgumentException | JMSException e) {
            throw new GlobalCustomException("Member not found with ID: " + memberId, 404);
        }
    }


    // Retrieve a member's benefits
    @Override
    public List<String> getMemberBenefits(String memberId) throws GlobalCustomException {

        // Find the member in the database
        Document member = myMongoClient.findDocumentById(memberId, "memberAccount");

        // Check if the member exists
        if (member == null) {
            throw new GlobalCustomException("Member not found.", 404);
        }


        // Find the member's benefits in the database
        Document memberBenefitsDocument = myMongoClient.findDocumentById(memberId, "memberBenefits");

        // Check if the member has benefits
        if (memberBenefitsDocument != null) {
            // Get the list of benefits
            List<String> memberBenefits = memberBenefitsDocument.get("benefit", List.class);

            // Check if the list is empty
            if (memberBenefits == null || memberBenefits.isEmpty()) {
                throw new GlobalCustomException("Member has no benefits.", 404);
            } else {
                return memberBenefits;
            }
        } else {
            throw new GlobalCustomException("Member has no benefits.", 404);
        }
    }
}