package com.banzai.fileloader.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class TaskRunner {

    @Autowired
    private ExecutorService executorService;

    @Scheduled(fixedRateString = "${extractor.pollingFrequency}")
    private void run() {
        log.info("Polling for tasks to run");

    }

}
