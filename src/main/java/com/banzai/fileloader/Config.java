package com.banzai.fileloader;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.Marshaller;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(SchedulerProperties.class)
@Slf4j
public class Config {

    @Bean
    public ExecutorService executor(SchedulerProperties properties) {
        return Executors.newFixedThreadPool(properties.workerThreads);
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(com.banzai.fileloader.entity.external.ContentXml.class);
        marshaller.setMarshallerProperties(new HashMap<String, Object>() {{
            put(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
            put(Marshaller.JAXB_SCHEMA_LOCATION, "xml/content_schema.xsd");
            }
        });

        return marshaller;
    }

}
