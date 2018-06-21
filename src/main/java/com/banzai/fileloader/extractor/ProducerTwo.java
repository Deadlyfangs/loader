package com.banzai.fileloader.extractor;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

@Slf4j
@RequiredArgsConstructor
public class ProducerTwo implements Runnable {

    private final BlockingQueue<String> queue;
    private final Queue<String> waitList;

    @Override
    public void run() {
        log.info("Producer started fetching waitlist...");
        fetchWaitList();
    }

    private void fetchWaitList() {
        while(!waitList.isEmpty()) {
            String filePath = waitList.poll();
            putIntoQueue(getContent(filePath));
        }
    }

    private String getContent(String filePath) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private void putIntoQueue(String content) {
        try {
            queue.put(content);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
