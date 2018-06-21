package com.banzai.fileloader.extractor;


import com.banzai.fileloader.Entity.internal.ContentEntity;
import com.banzai.fileloader.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class ConsumerTwo implements Runnable {

    private final BlockingQueue<String> queue;
    private final ContentRepository contentRepository;

    @Override
    public void run() {
        log.info("Consumer started fetching waitlist...");
        fetchQueue();
    }

    private void fetchQueue() {
        String content = takeFromQueue();

        if(isValid(content)) {
            save(parse(content));
        }
    }

    private String takeFromQueue() {
        String content = null;
        try {
            content = queue.poll(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return content;
    }

    private boolean isValid(String content) {
        return true;
    }

    private ContentEntity parse(String content) {
        return new ContentEntity();
    }

    private void save(ContentEntity contentEntity) {
        contentRepository.save(contentEntity);
    }

}
