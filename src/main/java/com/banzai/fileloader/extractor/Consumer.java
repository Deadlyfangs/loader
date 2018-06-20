package com.banzai.fileloader.extractor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;

@Slf4j
@RequiredArgsConstructor
public class Consumer implements Runnable {

    private final BlockingQueue<String> fileQueue;

    @Override
    public void run() {
        try {
            log.info("Consumer {} sleeps for 3 sec.");
            Thread.sleep(3000);
            log.info("Consumer {} awakens.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
