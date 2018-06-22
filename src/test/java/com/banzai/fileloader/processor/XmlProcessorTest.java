package com.banzai.fileloader.processor;


import com.banzai.fileloader.Entity.external.ContentXml;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class XmlProcessorTest {

    JAXBContext jaxbContext = JAXBContext.newInstance(ContentXml.class);

    Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).
        newSchema(this.getClass().getClassLoader().getResource("xml/content_schema.xsd"));

    XmlProcessor xmlProcessor = new XmlProcessor(jaxbContext, schema);

    public XmlProcessorTest() throws JAXBException, SAXException {
    }

    @Test
    public void testUnmarshal() {

        try {
            xmlProcessor.unmarshal(new File("/Users/d.diallo/BanzaiFolder/New/test.xml"));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}
