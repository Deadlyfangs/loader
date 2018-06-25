package com.banzai.fileloader.parser;


import com.banzai.fileloader.entity.external.ContentXml;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.JAXBException;
import java.io.File;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class XmlProcessorTest {

    @Autowired
    JaxbContextLoader jaxbContextLoader;

    public XmlProcessorTest() {
    }

    @Test
    public void testUnmarshal() {

        XmlProcessor xmlProcessor = new XmlProcessor(jaxbContextLoader.getJaxbContext(), jaxbContextLoader.getSchema());

        try {
            ContentXml content = xmlProcessor.unmarshal(new File("/Users/d.diallo/BanzaiFolder/New/test.xml"));

            File file = xmlProcessor.marshal(content);

            log.debug("Done");
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }


}
