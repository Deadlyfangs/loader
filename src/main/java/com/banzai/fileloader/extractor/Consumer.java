package com.banzai.fileloader.extractor;


import com.banzai.fileloader.entity.external.ContentXml;
import com.banzai.fileloader.entity.internal.ContentEntity;
import com.banzai.fileloader.exception.XmlFormatException;
import com.banzai.fileloader.parser.XmlProcessor;
import com.banzai.fileloader.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

@Slf4j
@RequiredArgsConstructor
public class Consumer implements Runnable {

    private final BlockingQueue<File> queue;
    private final ContentRepository contentRepository;
    private final XmlProcessor xmlProcessor;
    private final Map<FolderType, Folder> folderMap;

    @Override
    public void run() {
        log.info("Consumer started fetching blocking queue...");

        if(queue.isEmpty()) {
            return;
        }

        File file = fetchQueue();

        try {
            ContentEntity newContentEntity = parse(file);
            save(newContentEntity);

            moveTo(file, FolderType.PROCESSED);
        } catch (XmlFormatException e) {
            moveTo(file, FolderType.ERROR);
            log.info(e.getMessage());
        }
    }

    private File fetchQueue() {
        File content = null;
        try {
            content = queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return content;
    }

    private ContentEntity parse(File content) throws XmlFormatException {
        ContentXml contentXml = xmlProcessor.unmarshal(content);

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
            }
            if (folderType.equals(FolderType.ERROR)) {
                Files.move(sourceDir, errorDir.resolve(sourceDir.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save(ContentEntity contentEntity) {
        contentRepository.save(contentEntity);
    }

}
