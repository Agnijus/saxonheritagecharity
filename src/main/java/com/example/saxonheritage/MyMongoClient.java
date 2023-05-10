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

    // Constructor for creating a new instance of MyMongoClient class.
    public MyMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    // Method for inserting a document in the specified collection.
    public void insertDocument(Document document, String collection) {
        MongoCollection<Document> myCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection(collection);
        myCollection.insertOne(document);
    }

    // Method for finding a document by ID in the specified collection.
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

    // Method for updating a document by ID in the specified collection.
    public void updateDocumentById(String id, String field, Object value, String collection) {
        MongoCollection<Document> myCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection(collection);
        myCollection.updateOne(Filters.eq("_id", new ObjectId(id)), Updates.set(field, value));
    }
    public void insertPromotion(String memberId, String promotion) throws GlobalCustomException {
        // Get the member and promotions collection from the MongoDB client
        MongoCollection<Document> memberCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection("memberService");
        MongoCollection<Document> promotionsCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection("memberPromotions");

        // Convert the member ID string to an ObjectId
        ObjectId objectId;
        try {
            objectId = new ObjectId(memberId);
        } catch (IllegalArgumentException e) {
            throw new GlobalCustomException("Invalid member ID.", 400);
        }

        // Check if the member exists in the member collection
        Document existingMember = memberCollection.find(new Document("_id", objectId)).first();
        if (existingMember == null) {
            throw new GlobalCustomException("Member not found.", 404);
        }

        // Check if the member already has the given promotion
        Document existingDocument = promotionsCollection.find(new Document("_id", objectId)).first();
        if (existingDocument != null) {
            // If the member already has the promotion, throw an exception
            List<String> existingPromotions = existingDocument.getList("promotions", String.class);
            if (existingPromotions.contains(promotion)) {
                throw new GlobalCustomException("Promotion already exists.", 409);
            }

            // If the member doesn't have the promotion, add it to the existing list of promotions
            existingPromotions.add(promotion);
            Document updatedDocument = new Document("$set", new Document("promotions", existingPromotions));
            promotionsCollection.updateOne(new Document("_id", objectId), updatedDocument);
        } else {
            // If the member doesn't have any promotions yet, create a new document with the member ID and the new promotion
            Document newDocument = new Document("_id", objectId).append("promotions", Collections.singletonList(promotion));
            promotionsCollection.insertOne(newDocument);
        }
    }


    // Method for inserting visitor reports for a member.
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

        // If there are existing visitor reports for the member, check if the new visitor reports already exist and add them to the existing list.
        if (existingDocument != null) {
            List<String> existingVisitorReports = existingDocument.getList("visitorReports", String.class);

            for (String visitorReport : visitorReports) {
                if (existingVisitorReports.contains(visitorReport)) {
                    throw new GlobalCustomException("Visitor report already exists.", 409);
                }
                existingVisitorReports.add(visitorReport);
            }

            // Update the existing visitor reports document with the new visitor reports.
            Document updatedDocument = new Document("$set", new Document("visitorReports", existingVisitorReports));
            visitorReportsCollection.updateOne(new Document("_id", objectId), updatedDocument);
        } else {
            // If there are no existing visitor reports for the member, create a new document with the member's ID and the new visitor reports.
            Document newDocument = new Document("_id", objectId).append("visitorReports", visitorReports);
            visitorReportsCollection.insertOne(newDocument);
        }
    }
}



