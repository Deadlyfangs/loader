package com.banzai.fileloader.extractor;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ConsumerTest {

    @Test
    public void test(){
        File file = Paths.get("/Users/d.diallo/BanzaiFolder/New").toFile();

        Path source = file.toPath();
        Path dest = Paths.get(file.getPath() + "/processed");

        try {
            Files.createDirectory(dest);
        } catch (FileAlreadyExistsException e) {
            log.info("Folder wuth name: {} already exists.", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }


        log.info("Done");

    }

}
