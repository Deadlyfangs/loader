package com.banzai.fileloader.extractor;


import com.banzai.fileloader.entity.external.ContentXml;
import com.banzai.fileloader.entity.internal.ContentEntity;
import com.banzai.fileloader.parser.XmlValidationEventHandler;
import com.banzai.fileloader.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

@Component
@Scope("prototype")
@Slf4j
@RequiredArgsConstructor
public class Consumer implements Runnable {

    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private Jaxb2Marshaller marshaller;

    private final BlockingQueue<File> queue;
    private final Map<FolderType, Folder> folderMap;


    @Override
    public void run() {
        log.debug("Consumer started fetching blocking queue...");

        if(queue.isEmpty()) {
            return;
        }

        File file = fetchQueue();
        try {
            ContentEntity newContentEntity = parse(file);
            save(newContentEntity);
            moveTo(file, FolderType.PROCESSED);
        } catch (XmlMappingException e) {
            moveTo(file, FolderType.ERROR);
        }
    }

    private File fetchQueue() {
        File content = null;
        try {
            content = queue.take();
        } catch (InterruptedException e) {
            log.warn("Consumer task is interrupted due to \"{}\"",e.getMessage());
        }
        return content;
    }

    private ContentEntity parse(File content) throws XmlMappingException {
        marshaller.setValidationEventHandler(new XmlValidationEventHandler());
        ContentXml contentXml = (ContentXml) marshaller.unmarshal(new StreamSource(content));

        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setContent(contentXml.getContent());
        contentEntity.setCreationDate(contentXml.getCreationDate());

        return contentEntity;
    }

    private void moveTo(File content, FolderType folderType) {
        Path sourceDir = Paths.get(content.getPath());
        Path processedDir = Paths.get(folderMap.get(FolderType.PROCESSED).getPath());
        Path errorDir = Paths.get(folderMap.get(FolderType.ERROR).getPath());

        try {
            if (folderType.equals(FolderType.PROCESSED)) {
                Files.move(sourceDir, processedDir.resolve(sourceDir.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                log.info("File \"{}\" is processed", content.getName());
            }
            if (folderType.equals(FolderType.ERROR)) {
                Files.move(sourceDir, errorDir.resolve(sourceDir.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                log.error("Could not process file \"{}\". {}", content.getName());
            }
        } catch (IOException e) {
            log.error("Could not move processed file \"{}\". Exception: {}", content.getName(), e.getMessage());
        }
    }

    private void save(ContentEntity contentEntity) {
        contentRepository.save(contentEntity);
    }

}
