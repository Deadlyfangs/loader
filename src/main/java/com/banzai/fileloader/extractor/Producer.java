package com.banzai.fileloader.extractor;

import com.banzai.fileloader.ExtractorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@RequiredArgsConstructor
public class Producer implements Runnable {

    private final BlockingQueue<String> fileQueue;
    private final String source;

    private List<File> scanDirectory() {
        List<File> fileList = null;
        String fileDirectory = String.valueOf(source);

        try (Stream<Path> paths = Files.walk(Paths.get(fileDirectory))) {
            fileList = paths
                .filter(Files::isRegularFile) //cut symbolic links
                .filter(f -> f.toString().endsWith(".xml")) //only XML format
                .map(Path::toFile)
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return fileList;
    }

    @Override
    public void run() {

        scanDirectory().forEach(f -> {
            try {
                log.info(f.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
//        try {
//            log.info("Producer {} sleeps for 1 sec.");
//            Thread.sleep(1000);
//            log.info("Producer {} awakens.");
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

}
