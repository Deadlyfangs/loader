package com.banzai.fileloader.processor;

import com.banzai.fileloader.Entity.external.ContentXml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

@Component
@Slf4j
public class XmlProcessor {

    private JAXBContext context;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private SchemaFactory schemaFactory;
    private Schema schema;

    @PostConstruct
    public void prepare() {
        log.info("Preparing {}", XmlProcessor.class.getCanonicalName());
        try {
            context = JAXBContext.newInstance(ContentXml.class);
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
            schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schema = schemaFactory.newSchema(new File("xml/content_schema.xsd"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File marshal(ContentXml contentXml) {
        File content = new File(String.valueOf(contentXml.getCreationDate()));

        try {
            marshaller.marshal(contentXml, content);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return content;
    }

    public ContentXml unmarshal(File content) {
        try {
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(new XmlValidationEventHandler());

            ContentXml contentXml = (ContentXml) unmarshaller.unmarshal(content);

            return contentXml;
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return null;
    }

}
