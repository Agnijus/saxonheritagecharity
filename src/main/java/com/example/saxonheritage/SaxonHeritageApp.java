package com.example.saxonheritage;

import com.example.saxonheritage.jms.CustomMessageConsumer;
import com.example.saxonheritage.services.BenefitsService;
import com.example.saxonheritage.services.MemberServiceImpl;
import com.example.saxonheritage.services.BenefitsServiceImpl;
import com.example.saxonheritage.MyMongoClient;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


import javax.jms.JMSException;
import javax.xml.ws.Endpoint;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@SpringBootApplication
@ComponentScan({"com.example.saxonheritage", "com.example.saxonheritage.config"})
public class SaxonHeritageApp implements WebMvcConfigurer {

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
    private MemberServiceImpl memberService;

    public static void main(String[] args) {
        SpringApplication.run(SaxonHeritageApp.class, args);
    }



    @Bean
    public MongoClient mongoClient() {
        MongoCredential credential = MongoCredential.createScramSha1Credential(
                "dbUser",
                "SaxonHeritageApp",
                "dbUser".toCharArray()
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb+srv://dbUser:dbUser@cluster0.lombu.mongodb.net/?retryWrites=true&w=majority"))
                .credential(credential)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public ServletRegistrationBean<CXFServlet> cxfServletRegistration() {
        ServletRegistrationBean<CXFServlet> registrationBean = new ServletRegistrationBean<>(new CXFServlet(), "/soap-api/*");
        registrationBean.setName("cxfServletRegistration");
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/soap-api/*");
        return registrationBean;
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }


    @Bean
    @Autowired
    public Endpoint endpoint(MemberServiceImpl memberService) {
        EndpointImpl endpoint = new EndpointImpl(springBus(), memberService);
        endpoint.publish("/memberService");
        return endpoint;
    }

    @Bean
    public Endpoint benefitsEndpoint(@Autowired BenefitsService benefitsService) {
        EndpointImpl endpoint = new EndpointImpl(springBus(), benefitsService);
        endpoint.publish("/benefitsService");
        return endpoint;
    }


    @Bean(initMethod = "init")
    public CustomMessageConsumer customMessageConsumer(@Autowired MyMongoClient myMongoClient) {
        return new CustomMessageConsumer(myMongoClient);
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/error.html")
                .addResourceLocations("classpath:/static/");
    }
}
