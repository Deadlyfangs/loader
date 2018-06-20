package com.banzai.fileloader.scheduler;

import com.banzai.fileloader.ExtractorProperties;
import com.banzai.fileloader.extractor.Consumer;
import com.banzai.fileloader.extractor.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

@Service
@Slf4j
public class TaskRunner {

    private String source;
    private int producers;
    private int consumers;
    private BlockingQueue<String> queue;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    ExtractorProperties extractorProperties;

    @PostConstruct
    private void init() {
        source = extractorProperties.getDirectory().getSource();
        producers = extractorProperties.getProducers();
        consumers = Runtime.getRuntime().availableProcessors();
        queue = new LinkedBlockingDeque<String>(extractorProperties.getQueueBound());
    }

    @Scheduled(fixedRateString = "${extractor.pollingFrequency}")
    private void run() {
        log.info("Polling for tasks to run. Source directory: {}", source);

        int producerCount = 0;
        for(int p = 0; p < producers; p++) {
            final int count = producerCount++;
            executorService.execute(new Producer(queue, source));
        }

        int consumerCount = 0;
        for(int c = 0; c < consumers; c++) {
            final int count = consumerCount++;
            executorService.execute(new Consumer(queue));
        }
    }

}
