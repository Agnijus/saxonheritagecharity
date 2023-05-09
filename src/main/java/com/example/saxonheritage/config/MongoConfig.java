package com.example.saxonheritage.config;

import com.example.saxonheritage.MyMongoClient;
import com.example.saxonheritage.services.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

    private final String mongoUri;
    private final String mongoUsername;
    private final String mongoPassword;

    public MongoConfig(@Value("${mongo.uri}") String mongoUri,
                       @Value("${mongo.username}") String mongoUsername,
                       @Value("${mongo.password}") String mongoPassword) {
        this.mongoUri = mongoUri;
        this.mongoUsername = mongoUsername;
        this.mongoPassword = mongoPassword;
    }

    @Bean
    public MongoClient mongoClient() {
        MongoCredential credential = MongoCredential.createCredential(mongoUsername, "SaxonHeritageApp", mongoPassword.toCharArray());
        MongoClient mongoClient = MongoClients.create(new ConnectionString(mongoUri));
        return mongoClient;
    }
    @Bean
    public MyMongoClient myMongoClient(MongoClient mongoClient) {
        return new MyMongoClient(mongoClient);
    }
    @Bean
    public MemberServiceImpl memberServiceImpl(MyMongoClient myMongoClient) {
        return new MemberServiceImpl(myMongoClient);
    }
}
