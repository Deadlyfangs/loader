package com.example.file.processor.scheduler;


import com.example.file.processor.SchedulerProperties;
import com.example.file.processor.extractor.Consumer;
import com.example.file.processor.extractor.Folder;
import com.example.file.processor.extractor.FolderType;
import com.example.file.processor.extractor.Producer;
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
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Profile("!test")
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskRunner {

    private final SchedulerProperties extractorProperties;
    private final ExecutorService executorService;
    private final BiFunction<BlockingQueue<File>, Map<FolderType, Folder>, Consumer> consumerFactory;
    private final BiFunction<BlockingQueue<File>, Queue<String>, Producer> producerFactory;

    private BlockingQueue<File> queue;
    private Lock lock = new ReentrantLock();
    private Queue<String> waitList = new ConcurrentLinkedQueue<>();
    private Map<String, String> scannedList = new ConcurrentHashMap<>();
    private Map<FolderType, Folder> folderMap;

    @PostConstruct
    private void init() {
        setQueueBound();
        createDirectories();
        setFolders();
    }

    @Scheduled(fixedRateString = "${scheduler.pollingFrequency}")
    private void run() {
        log.info("Scanning source directory: {}", getSourceDir());

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
                    if(!scannedList.containsKey(s)) {
                        scannedList.put(s, null);
                        waitList.offer(s);
                    }
                });

        for(int i = 0; i < getProducerCount(); i++) {
            executorService.submit(producerFactory.apply(queue, waitList));
        }
        for (int i = 0; i < getConsumerCount(); i++) {
            executorService.submit(consumerFactory.apply(queue, folderMap));
        }
    }

    private List<String> scanDirectory() {
        List<String> filePathList = Collections.emptyList();

        try (Stream<Path> paths = Files.walk(Paths.get(getSourceDir()))) {
            filePathList = paths
                    .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
                    .filter(path -> path.toString().endsWith(".xml"))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (NoSuchFileException ne) {
            log.warn("File has already been processed. Exception: {}", ne.getMessage());
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
            Files.createDirectories(sourceDir);
        } catch (FileAlreadyExistsException e) {
            log.info("FolderType with name: {} already exists.", e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        try {
            Files.createDirectories(processedDir);
        } catch (FileAlreadyExistsException e) {
            log.info("FolderType with name: {} already exists.", e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }

        try {
            Files.createDirectories(errorDir);
        } catch (FileAlreadyExistsException e) {
            log.info("FolderType with name: {} already exists.", e.getMessage());
        } catch (IOException e) {
            log.info(e.getMessage());
        }
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

    private void setFolders() {
        folderMap = new HashMap<>();
        folderMap.put(FolderType.SOURCE, new Folder(FolderType.SOURCE, getSourceDir()));
        folderMap.put(FolderType.PROCESSED, new Folder(FolderType.PROCESSED, getProcessedDir()));
        folderMap.put(FolderType.ERROR, new Folder(FolderType.ERROR, getErrorDir()));
    }

}
