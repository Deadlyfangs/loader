package com.banzai.fileloader;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties(ExtractorProperties.class)
@Slf4j
public class Config {

    @Bean
    public ExecutorService executor(ExtractorProperties properties) {
        return Executors.newFixedThreadPool(properties.workerThreads);
    }

}
