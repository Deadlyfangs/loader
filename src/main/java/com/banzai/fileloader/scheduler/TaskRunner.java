package com.banzai.fileloader.scheduler;


import com.banzai.fileloader.SchedulerProperties;
import com.banzai.fileloader.extractor.Consumer;
import com.banzai.fileloader.extractor.Folder;
import com.banzai.fileloader.extractor.FolderType;
import com.banzai.fileloader.extractor.Producer;
import com.banzai.fileloader.parser.JaxbContextLoader;
import com.banzai.fileloader.parser.XmlProcessor;
import com.banzai.fileloader.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Profile("!test")
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskRunner {

    private final SchedulerProperties extractorProperties;
    private final ExecutorService executorService;
    private final ContentRepository contentRepository;
    private final JaxbContextLoader jaxbContextLoader;

    private Lock lock = new ReentrantLock();
    private BlockingQueue<File> queue;
    private Queue<String> waitList = new ConcurrentLinkedQueue<>();
    private List<String> scannedList = new ArrayList<>(10000);
    private Map<FolderType, Folder> folderMap;

    @PostConstruct
    private void init() {
        setQueueBound();
        createDirectories();
    }

    @Scheduled(fixedRateString = "${scheduler.pollingFrequency}")
    private void run() {
        log.info("Polling for tasks to run. Source directory: {}", getSourceDir());

        if (lock.tryLock()) {
            try {
                process();
            } catch (Throwable t) {
                log.error(t.getMessage());
            } finally {
                lock.unlock();
            }
        }
    }

    private void process() {
        scanDirectory().stream()
                .forEach(s -> {
                    if (!scannedList.contains(s)) {
                        scannedList.add(s);
                        waitList.offer(s);
                    }
                });

        for(int i = 0; i < getProducerCount(); i++) {
            executorService.submit(createProducer());
        }
        for (int i = 0; i < getConsumerCount(); i++) {
            executorService.submit(createConsumer());
        }
    }

    private List<String> scanDirectory() {
        List<String> filePathList = Collections.emptyList();

        try (Stream<Path> paths = Files.walk(Paths.get(getSourceDir()))) {
            filePathList = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".xml"))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (NoSuchFileException ne) {
            log.warn(ne.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return filePathList;
    }

    private void createDirectories() {
        Path sourceDir = Paths.get(getSourceDir());
        Path processedDir = Paths.get(getProcessedDir());
        Path errorDir = Paths.get(getErrorDir());

        try {
            Files.createDirectory(sourceDir);
        } catch (FileAlreadyExistsException e) {
            log.info("FolderType with name: {} already exists.", e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        try {
            Files.createDirectory(processedDir);
        } catch (FileAlreadyExistsException e) {
            log.info("FolderType with name: {} already exists.", e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        try {
            Files.createDirectory(errorDir);
        } catch (FileAlreadyExistsException e) {
            log.info("FolderType with name: {} already exists.", e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }

    private Producer createProducer() {
        return new Producer(queue, waitList);
    }

    private Consumer createConsumer() {
        return new Consumer(queue, contentRepository, getXmlProcessor(), folderMap);
    }

    private XmlProcessor getXmlProcessor() {
        return new XmlProcessor(jaxbContextLoader.getJaxbContext(), jaxbContextLoader.getSchema());
    }

    private void setFolders() {
        folderMap = new HashMap<>();
        folderMap.put(FolderType.SOURCE, new Folder(FolderType.SOURCE, getSourceDir()));
        folderMap.put(FolderType.PROCESSED, new Folder(FolderType.PROCESSED, getProcessedDir()));
        folderMap.put(FolderType.ERROR, new Folder(FolderType.ERROR, getErrorDir()));
    }

    //Get&Set main parameters
    private String getSourceDir() {
        return extractorProperties.getDirectory().getSource();
    }

    private String getProcessedDir() {
        return extractorProperties.getDirectory().getProcessed();
    }

    private String getErrorDir() {
        return extractorProperties.getDirectory().getError();
    }

    private int getProducerCount() {
        return extractorProperties.getProducers();
    }

    private int getConsumerCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    private void setQueueBound() {
        queue = new LinkedBlockingDeque<>(extractorProperties.getQueueBound());
    }

}
