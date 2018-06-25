package com.banzai.fileloader.extractor;


import com.banzai.fileloader.entity.external.ContentXml;
import com.banzai.fileloader.entity.internal.ContentEntity;
import com.banzai.fileloader.parser.XmlProcessor;
import com.banzai.fileloader.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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

        File file = fetchQueue();

        try {
            ContentEntity newContentEntity = parse(file);
            save(newContentEntity);
            moveTo(file, Folder.PROCESSED);
        } catch (JAXBException e) {
            moveTo(file, Folder.ERROR);
            e.printStackTrace();
        }
    }

    private File fetchQueue() {
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

    private void moveTo(File content, Folder folder){
        String source = content.getPath();

        Path sourceDir = Paths.get(source);
        Path processedDir = Paths.get(source + "/processed");
        Path errorDir = Paths.get(source + "/error");

        try {
            if (folder.equals(Folder.PROCESSED)) {
                Files.move(sourceDir, processedDir, StandardCopyOption.REPLACE_EXISTING);
            }
            if (folder.equals(Folder.ERROR)) {
                Files.move(sourceDir, errorDir, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
