package com.example.file.processor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "scheduler")
@Getter
@Setter
public class SchedulerProperties {

    int workerThreads;
    int producers;
    int queueBound;
    long pollingFrequency;


    @NestedConfigurationProperty
    Directory directory = new Directory();

    @Getter
    @Setter
    public static class Directory {
        String source;
        String processed;
        String error;
    }

}

