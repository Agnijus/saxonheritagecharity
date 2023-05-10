// MongoDB configuration class for Saxon Heritage Charity
package com.example.saxonheritagecharity.config;

import com.example.saxonheritagecharity.MyMongoClient;
import com.example.saxonheritagecharity.services.MemberServiceImplementation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mongodb.MongoCredential;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfiguration {

    private final String mongodbUri;
    private final String mongodbUsername;
    private final String mongodbPassword;

    // Constructor for MongoDB configuration with injected property values
    public MongoConfiguration(@Value("${mongo.uri}") String mongodbUri,
                              @Value("${mongo.username}") String mongodbUsername,
                              @Value("${mongo.password}") String mongodbPassword) {
        this.mongodbUri = mongodbUri;
        this.mongodbUsername = mongodbUsername;
        this.mongodbPassword = mongodbPassword;
    }

    // Creates and returns a MongoDB client instance
    @Bean
    public MongoClient createMongoClient() {
        MongoCredential credential = MongoCredential.createCredential(mongodbUsername, "SaxonHeritageApp", mongodbPassword.toCharArray());
        MongoClient mongoClient = MongoClients.create(new ConnectionString(mongodbUri));
        return mongoClient;
    }

    // Creates and returns an instance of the custom MongoDB client
    @Bean
    public MyMongoClient customMongoClient(MongoClient mongoClient) {
        return new MyMongoClient(mongoClient);
    }

    // Creates and returns an instance of the MemberService implementation
    @Bean
    public MemberServiceImplementation memberServiceImplementation(MyMongoClient myMongoClient) {
        return new MemberServiceImplementation(myMongoClient);
    }
}