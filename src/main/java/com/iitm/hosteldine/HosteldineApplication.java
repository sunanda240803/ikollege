package com.iitm.hosteldine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource({"classpath:messages.properties", "classpath:url-text.properties", "classpath:label-text.properties"})
public class HosteldineApplication {

    public static void main(String[] args) {
        System.setProperty("jakarta.xml.soap.SOAPConnectionFactory",
                "com.sun.xml.messaging.saaj.client.p2p.HttpSOAPConnectionFactory");
        SpringApplication.run(HosteldineApplication.class, args);
    }

}
