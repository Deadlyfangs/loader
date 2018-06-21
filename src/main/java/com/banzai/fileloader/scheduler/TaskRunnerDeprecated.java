//package com.banzai.fileloader.scheduler;
//
//import com.banzai.fileloader.ExtractorProperties;
//
//import com.banzai.fileloader.extractor.ConsumerDeprecated;
//import com.banzai.fileloader.extractor.ProducerDeprecated;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.LinkedBlockingDeque;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Service
//@Slf4j
//public class TaskRunnerDeprecated {
//
//    private String source;
//    private int producers;
//    private int consumers;
//    private BlockingQueue<String> queue;
//
//    @Autowired
//    private ExecutorService executorService;
//
//    @Autowired
//    ExtractorProperties extractorProperties;
//
//    @PostConstruct
//    private void init() {
//        source = extractorProperties.getDirectory().getSource();
//        producers = extractorProperties.getProducers();
//        consumers = Runtime.getRuntime().availableProcessors();
//        queue = new LinkedBlockingDeque<String>(extractorProperties.getQueueBound());
//    }
//
//    @Scheduled(fixedRateString = "${extractor.pollingFrequency}")
//    private void run() {
//        log.info("Polling for tasks to run. Source directory: {}", source);
//
//        for(File file : scanDirectory()) {
//            executorService.execute(new ProducerDeprecated(queue, source));
//        }
//
//        for(int c = 0; c < consumers; c++) {
//            executorService.execute(new ConsumerDeprecated(queue));
//        }
//    }
//
//    private List<File> scanDirectory() {
//        List<File> fileList = Collections.emptyList();
//        String fileDirectory = String.valueOf(source);
//
//        try (Stream<Path> paths = Files.walk(Paths.get(fileDirectory))) {
//            fileList = paths
//                    .filter(Files::isRegularFile)
//                    .filter(f -> f.toString().endsWith(".xml"))
//                    .map(Path::toFile)
//                    .collect(Collectors.toList());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return fileList;
//    }
//
//}
