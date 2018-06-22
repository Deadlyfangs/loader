package com.banzai.fileloader.scheduler;


import com.banzai.fileloader.ExtractorProperties;
import com.banzai.fileloader.extractor.ConsumerTwo;
import com.banzai.fileloader.extractor.ProducerTwo;
import com.banzai.fileloader.processor.JaxbContextLoader;
import com.banzai.fileloader.processor.XmlProcessor;
import com.banzai.fileloader.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Profile("!test")
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskRunnerTwo {

    private final ExtractorProperties extractorProperties;
    private final ExecutorService executorService;
    private final ContentRepository contentRepository;
    private final JaxbContextLoader jaxbContextLoader;

    private String source;
    private int producers;
    private int consumers;
    private BlockingQueue<File> queue;
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

        if(state.equals(State.FREE)) {
            state = State.BUSY;

            process();

            for(int i = 0; i < producers; i++) {
                executorService.submit(new ProducerTwo(queue, waitList));
            }
            for (int i = 0; i < consumers; i++) {
                executorService.submit(new ConsumerTwo(queue, contentRepository,
                        new XmlProcessor(jaxbContextLoader.getJaxbContext(), jaxbContextLoader.getSchema())));
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
