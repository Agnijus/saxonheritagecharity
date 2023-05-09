package com.example.saxonheritage;

import com.example.saxonheritage.model.Member;
import com.example.saxonheritage.services.GlobalCustomException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import com.google.gson.Gson;

@Component
public class MyMongoClient {
    private final MongoClient mongoClient;
    private static final String DATABASE_NAME = "SaxonHeritageApp";

    public MyMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }


    // Insert element
    public void insertDocument(Document document, String collection) {
        MongoCollection<Document> myCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection(collection);
        myCollection.insertOne(document);
    }

    // Find by ID
    public Document findDocumentById(String id, String collection) {
        System.out.println("findDocumentById: id = " + id + ", collection = " + collection); // Debug log
        MongoCollection<Document> myCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection(collection);
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid ObjectId: " + id); // Debug log
            return null;
        }
        Document document = myCollection.find(Filters.eq("_id", objectId)).first();
        if (document == null) {
            System.out.println("No document found with id = " + id); // Debug log
        } else {
            System.out.println("Found document: " + document.toJson()); // Debug log
        }
        return document;
    }



    // Update by ID
    public void updateDocumentById(String id, String field, Object value, String collection) {
        MongoCollection<Document> myCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection(collection);
        myCollection.updateOne(Filters.eq("_id", new ObjectId(id)), Updates.set(field, value));
    }


    public void insertVisitorReports(String memberId, List<String> visitorReports) throws GlobalCustomException {
        MongoCollection<Document> memberCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection("memberService");
        MongoCollection<Document> visitorReportsCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection("visitorReports");

        ObjectId objectId;
        try {
            objectId = new ObjectId(memberId);
        } catch (IllegalArgumentException e) {
            throw new GlobalCustomException("Invalid member ID.", 400);
        }

        Document existingMember = memberCollection.find(new Document("_id", objectId)).first();

        if (existingMember == null) {
            throw new GlobalCustomException("Member not found.", 404);
        }

        Document existingDocument = visitorReportsCollection.find(new Document("_id", objectId)).first();

        if (existingDocument != null) {
            List<String> existingVisitorReports = existingDocument.getList("visitorReports", String.class);

            for (String visitorReport : visitorReports) {
                if (existingVisitorReports.contains(visitorReport)) {
                    throw new GlobalCustomException("Visitor report already exists.", 409);
                }
                existingVisitorReports.add(visitorReport);
            }

            Document updatedDocument = new Document("$set", new Document("visitorReports", existingVisitorReports));
            visitorReportsCollection.updateOne(new Document("_id", objectId), updatedDocument);
        } else {
            Document newDocument = new Document("_id", objectId).append("visitorReports", visitorReports);
            visitorReportsCollection.insertOne(newDocument);
        }
    }




    public void insertPromotion(String memberId, String promotion) throws GlobalCustomException {
        MongoCollection<Document> memberCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection("memberService");
        MongoCollection<Document> promotionsCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection("memberPromotions");

        ObjectId objectId;
        try {
            objectId = new ObjectId(memberId);
        } catch (IllegalArgumentException e) {
            throw new GlobalCustomException("Invalid member ID.", 400);
        }

        Document existingMember = memberCollection.find(new Document("_id", objectId)).first();

        if (existingMember == null) {
            throw new GlobalCustomException("Member not found.", 404);
        }

        Document existingDocument = promotionsCollection.find(new Document("_id", objectId)).first();

        if (existingDocument != null) {
            List<String> existingPromotions = existingDocument.getList("promotions", String.class);

            if (existingPromotions.contains(promotion)) {
                throw new GlobalCustomException("Promotion already exists.", 409);
            }

            existingPromotions.add(promotion);
            Document updatedDocument = new Document("$set", new Document("promotions", existingPromotions));
            promotionsCollection.updateOne(new Document("_id", objectId), updatedDocument);
        } else {
            Document newDocument = new Document("_id", objectId).append("promotions", Collections.singletonList(promotion));
            promotionsCollection.insertOne(newDocument);
        }
    }
}
