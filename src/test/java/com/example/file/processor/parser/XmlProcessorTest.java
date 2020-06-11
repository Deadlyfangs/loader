package com.example.file.processor.parser;


import com.example.file.processor.exception.XmlFormatException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class XmlProcessorTest {

    public XmlProcessorTest() {
    }

    @Test
    public void testUnmarshal() throws XmlFormatException {

//        XmlProcessor xmlProcessor = new XmlProcessor(jaxbContextLoader.getJaxbContext(), jaxbContextLoader.getSchema());
//        String testFilePath = this.getClass().getClassLoader().getResource("xml/test.xml").toString();
//
//        ContentXml content = xmlProcessor.unmarshal(new File(testFilePath));

    }
}
