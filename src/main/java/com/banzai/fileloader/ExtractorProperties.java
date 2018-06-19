package com.banzai.fileloader;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "extractor")
@Getter
@Setter
public class ExtractorProperties {

    int workerThreads;
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

