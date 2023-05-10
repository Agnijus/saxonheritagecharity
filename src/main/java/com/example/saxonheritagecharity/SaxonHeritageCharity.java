// Agnijus Botyrius - 21466565
// Distributed Systems (CP60060E) - Assignment
// Project Title - Saxon Heritage Charity Application

package com.example.saxonheritagecharity;


import com.example.saxonheritagecharity.jms.CustomMessageReceiver;
import com.example.saxonheritagecharity.services.BenefitsService;
import com.example.saxonheritagecharity.services.MemberServiceImplementation;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.xml.ws.Endpoint;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan({"com.example.saxonheritagecharity", "com.example.saxonheritagecharity.config"})
public class SaxonHeritageCharity implements WebMvcConfigurer {

    // Read MongoDB credentials from application.properties
    @Value("${mongo.uri}")
    private String mongoUri;

    @Value("${mongo.username}")
    private String mongoUsername;

    @Value("${mongo.password}")
    private String mongoPassword;

    @Autowired
    @Lazy
    private BenefitsService benefitsService;

    @Autowired
    @Lazy
    private MemberServiceImplementation memberService;

    // Main method to run the Spring Boot application
    public static void main(String[] args) {
        SpringApplication.run(SaxonHeritageCharity.class, args);
    }

    // Create a MongoDB client with the given credentials
    @Bean
    public MongoClient mongoClient() {
        MongoCredential credential = MongoCredential.createScramSha1Credential(
                "dbUser",
                "SaxonHeritageCharity",
                "dbUser".toCharArray()
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .credential(credential)
                .build();

        return MongoClients.create(settings);
    }

    // Register the CXF servlet for handling SOAP requests
    @Bean
    public ServletRegistrationBean<CXFServlet> cxfServletRegistration() {
        ServletRegistrationBean<CXFServlet> registrationBean = new ServletRegistrationBean<>(new CXFServlet(), "/soap-api/*");
        registrationBean.setName("cxfServletRegistration");
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/soap-api/*");
        return registrationBean;
    }

    // Create a SpringBus bean for CXF to use
    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    // Register the member service endpoint with CXF
    @Bean
    @Autowired
    public Endpoint endpoint(MemberServiceImplementation memberService) {
        EndpointImpl endpoint = new EndpointImpl(springBus(), memberService);
        endpoint.publish("/memberService");
        return endpoint;
    }

    // Register the benefits service endpoint with CXF
    @Bean
    public Endpoint benefitsEndpoint(@Autowired BenefitsService benefitsService) {
        EndpointImpl endpoint = new EndpointImpl(springBus(), benefitsService);
        endpoint.publish("/benefitsService");
        return endpoint;
    }

    // Create a bean for CustomMessageReceiver that listens to the ActiveMQ queue for incoming messages
    // and interacts with the Mongo database to insert visitor reports and member promotions
    @Bean
    public CustomMessageReceiver customMessageReceiver(@Autowired MyMongoClient myMongoClient) {
        return new CustomMessageReceiver(myMongoClient);
    }

    // Override the default mapping for root endpoint to index.html
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    // Add resource handlers for error.html
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/error.html")
                .addResourceLocations("classpath:/static/");
    }
}
