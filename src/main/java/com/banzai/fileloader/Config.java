package com.banzai.fileloader;


import com.banzai.fileloader.extractor.Consumer;
import com.banzai.fileloader.extractor.Folder;
import com.banzai.fileloader.extractor.FolderType;
import com.banzai.fileloader.extractor.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.logging.Level;

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

        java.util.logging.Logger.getLogger("com.sun.xml.internal.bind").setLevel(Level.FINEST);

        return marshaller;
    }

    @Bean
    public BiFunction<BlockingQueue<File>, Map<FolderType, Folder>, Consumer> consumerFactory() {
        return (queue, folderMap) -> consumer(queue, folderMap);
    }

    @Bean
    @Scope(value = "prototype")
    public Consumer consumer(BlockingQueue queue, Map folderMap) {
        return new Consumer(queue, folderMap);
    }

    @Bean
    public BiFunction<BlockingQueue<File>, Queue<String>, Producer> producerFactory() {
        return (queue, waitList) -> producer(queue, waitList);
    }

    @Bean
    @Scope(value = "prototype")
    public Producer producer(BlockingQueue queue, Queue waitList) {
        return new Producer(queue, waitList);
    
}
