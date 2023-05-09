package com.example.saxonheritage.services;

import com.example.saxonheritage.jms.CustomMessageConsumer;
import com.example.saxonheritage.jms.CustomMessageProducer;
import com.example.saxonheritage.MyMongoClient;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BenefitsServiceImpl implements BenefitsService {
    private CustomMessageProducer customMessageProducer;
    private CustomMessageConsumer customMessageConsumer;
    private MyMongoClient myMongoClient;

    @Autowired
    public BenefitsServiceImpl(MyMongoClient myMongoClient) {
        this.myMongoClient = myMongoClient;
        customMessageProducer = new CustomMessageProducer();
        customMessageConsumer = new CustomMessageConsumer(myMongoClient);
        customMessageConsumer.init();
    }

        @Override
        public void addBenefit(String memberId, String benefit) throws GlobalCustomException {
            try {
                ObjectId memberIdObj = new ObjectId(memberId);
                Document memberDoc = myMongoClient.findDocumentById(memberId, "memberService");
                if (memberDoc != null) {
                    Document existingBenefit = myMongoClient.findDocumentById(memberId, "memberBenefits");
                    if (existingBenefit != null) {
                        List<String> existingBenefits = existingBenefit.getList("benefit", String.class);
                        if (existingBenefits != null && existingBenefits.contains(benefit)) {
                            throw new GlobalCustomException("Member already has this benefit.", 409);
                        } else {
                            if (existingBenefits.isEmpty()) {
                                existingBenefits = new ArrayList<>();
                            }
                            existingBenefits.add(benefit);
                            myMongoClient.updateDocumentById(memberId, "benefit", existingBenefits, "memberBenefits");
                            String benefitMessage = "Member ID: " + memberId + ", Benefit: " + benefit;
                            customMessageProducer.sendBenefitUpdate(benefitMessage, true);
                        }
                    } else {
                        List<String> benefitsList = new ArrayList<>();
                        benefitsList.add(benefit);
                        Document benefitDoc = new Document()
                                .append("_id", memberIdObj)
                                .append("benefit", benefitsList);
                        myMongoClient.insertDocument(benefitDoc, "memberBenefits");
                        String benefitMessage = "Member ID: " + memberId + ", Benefit: " + benefit;
                        customMessageProducer.sendBenefitUpdate(benefitMessage, true);
                    }
                } else {
                    throw new GlobalCustomException("Member not found with ID: " + memberId, 404);
                }
            } catch (IllegalArgumentException | JMSException e) {
                throw new GlobalCustomException("Member not found with ID: " + memberId, 404);
            }
        }

        @Override
        public void removeBenefit(String memberId, String benefit) throws GlobalCustomException {
            try {
                ObjectId memberIdObj = new ObjectId(memberId);
                Document memberDoc = myMongoClient.findDocumentById(memberId, "memberService");
                if (memberDoc != null) {
                    Document existingBenefit = myMongoClient.findDocumentById(memberId, "memberBenefits");
                    if (existingBenefit != null) {
                        List<String> existingBenefits = existingBenefit.getList("benefit", String.class);
                        if (existingBenefits != null && existingBenefits.contains(benefit)) {
                            existingBenefits.remove(benefit);
                            myMongoClient.updateDocumentById(memberId, "benefit", existingBenefits, "memberBenefits");
                            String benefitMessage = "Member ID: " + memberId + ", Benefit: " + benefit;
                            customMessageProducer.sendBenefitUpdate(benefitMessage, false);
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



                @Override
    public List<String> getMemberBenefits(String memberId) throws GlobalCustomException {
        System.out.println("Searching for member with ID: " + memberId); // Debug log

        Document member = myMongoClient.findDocumentById(memberId, "memberService");
        if (member == null) {
            throw new GlobalCustomException("Member not found.", 404);
        }
        System.out.println("Found member: " + member.toJson()); // Debug log

        Document memberBenefitsDocument = myMongoClient.findDocumentById(memberId, "memberBenefits");
        if (memberBenefitsDocument != null) {
            List<String> memberBenefits = memberBenefitsDocument.get("benefit", List.class);
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
