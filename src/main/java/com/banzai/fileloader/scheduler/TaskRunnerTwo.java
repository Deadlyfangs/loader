package com.banzai.fileloader.scheduler;


import com.banzai.fileloader.ExtractorProperties;
import com.banzai.fileloader.extractor.ConsumerTwo;
import com.banzai.fileloader.extractor.ProducerTwo;
import com.banzai.fileloader.repository.ContentRepository;
import javafx.beans.binding.ListBinding;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskRunnerTwo {

    private final ExtractorProperties extractorProperties;
    private final ExecutorService executorService;
    private final ContentRepository contentRepository;

    private String source;
    private int producers;
    private int consumers;
    private BlockingQueue<String> queue;
    private Queue<String> waitList = new ConcurrentLinkedQueue<>();
    private List<String> scannedList = new ArrayList<>(10000);
    private volatile State state = State.FREE;

    @PostConstruct
    private void init() {
        source = extractorProperties.getDirectory().getSource();
        producers = extractorProperties.getProducers();
        consumers = Runtime.getRuntime().availableProcessors();
        queue = new LinkedBlockingDeque<>(extractorProperties.getQueueBound());
    }

    @Scheduled(fixedRateString = "${extractor.pollingFrequency}")
    private void run() {
        log.info("Polling for tasks to run. Source directory: {}", source);
        log.info("State: {}", state);

        if(state.equals(State.FREE)) {
            state = State.BUSY;

            process();

            for(int i = 0; i < producers; i++) {
                executorService.submit(new ProducerTwo(queue, waitList));
            }

            for (int i = 0; i < consumers; i++) {
                executorService.submit(new ConsumerTwo(queue, contentRepository));
            }
            state = State.FREE;
        }

    }

    private List<String> scanDirectory() {
        List<String> filePathList = Collections.emptyList();
        String fileDirectory = String.valueOf(source);

        try (Stream<Path> paths = Files.walk(Paths.get(fileDirectory))) {
            filePathList = paths
                    .filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(".xml"))
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePathList;
    }

    private void process() {
        scanDirectory().stream()
                .forEach(s -> {
                    if (!scannedList.contains(s)) {
                        scannedList.add(s);
                        waitList.offer(s);
                    }
                });
    }

}
