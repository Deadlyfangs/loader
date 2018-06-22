package com.banzai.fileloader.extractor;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

@Slf4j
@RequiredArgsConstructor
public class Producer implements Runnable {

    private final BlockingQueue<File> queue;
    private final Queue<String> waitList;

    @Override
    public void run() {
        log.info("Producer started fetching waitlist...");
        fetchWaitList();
    }

    private void fetchWaitList() {
        while (!waitList.isEmpty()) {
            String filePath = waitList.poll();
            log.debug("FilePath: {}", filePath);
            putIntoQueue(getContent(filePath));
        }
    }

    private File getContent(String filePath) {
        return Paths.get(filePath).toFile();
    }

    private void putIntoQueue(File content) {
        try {
            queue.put(content);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
