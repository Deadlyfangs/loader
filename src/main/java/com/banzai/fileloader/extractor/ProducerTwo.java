package com.banzai.fileloader.extractor;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ProducerTwo implements Runnable {

    private final BlockingQueue<File> queue;
    private final Queue<String> waitList;

    @Override
    public void run() {
        log.info("Producer started fetching waitlist...");
        fetchWaitList();
    }

    private void fetchWaitList() {
        while(!waitList.isEmpty()) {
            String filePath = waitList.poll();
            log.info("FilePath: {}", filePath);
            putIntoQueue(getContent(filePath));
        }
    }

    private File getContent(String filePath) {
//        String content = null;

//        try {
//            content = Files.lines(Paths.get(filePath)).collect(Collectors.joining("\n"));
//            log.info("Content on path: {}", content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        File content = Paths.get(filePath).toFile();

        return content;
    }

    private void putIntoQueue(File content) {
        try {
            queue.put(content);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
