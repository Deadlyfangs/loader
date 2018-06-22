package com.banzai.fileloader.extractor;


import com.banzai.fileloader.Entity.external.ContentXml;
import com.banzai.fileloader.Entity.internal.ContentEntity;
import com.banzai.fileloader.parser.XmlProcessor;
import com.banzai.fileloader.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class Consumer implements Runnable {

    private final BlockingQueue<File> queue;
    private final ContentRepository contentRepository;
    private final XmlProcessor xmlProcessor;

    @Override
    public void run() {
        log.info("Consumer started fetching blocking queue...");

        File newFile = fetchQueue();
        try {
            ContentEntity newContentEntity = parse(newFile);
            save(newContentEntity);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private File fetchQueue() {
        return takeFromQueue();
    }

    private File takeFromQueue() {
        File content = null;
        try {
            content = queue.poll(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return content;
    }

    private ContentEntity parse(File content) throws JAXBException {
        ContentXml contentXml = xmlProcessor.unmarshal(content);

        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setContent(contentXml.getContent());
        contentEntity.setCreationDate(contentXml.getCreationDate());

        return contentEntity;
    }

    private void save(ContentEntity contentEntity) {
        contentRepository.save(contentEntity);
    }

}
