package com.banzai.fileloader.extractor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.BlockingQueue;


@Slf4j
@RequiredArgsConstructor
public class Producer implements Runnable {

    private final BlockingQueue<String> fileQueue;
    private final String source;

    private boolean validate(File file) {

        return true;
    }

    private void put(String contentXml) throws InterruptedException {
        fileQueue.put(contentXml);
    }

    @Override
    public void run() {


        try {
            log.info("Producer {} sleeps for 1 sec.");
            Thread.sleep(1000);
            log.info("Producer {} awakens.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
