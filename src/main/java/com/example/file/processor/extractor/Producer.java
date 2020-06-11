package com.example.file.processor.extractor;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class Producer implements Runnable {

    private final BlockingQueue<File> queue;
    private final Queue<String> waitList;

    @Override
    public void run() {
        log.debug("Producer started fetching waitlist...");

        File file = getContent(fetchWaitList());
        putIntoQueue(file);
    }

    private String fetchWaitList() {
        return waitList.poll();
    }

    private File getContent(String filePath) {
        return Paths.get(filePath).toFile();
    }

    private void putIntoQueue(File content) {
        try {
            queue.put(content);
        } catch (InterruptedException e) {
            log.warn("Producer task is interrupted due to \"{}\"", e.getMessage());
        }
    }

}
