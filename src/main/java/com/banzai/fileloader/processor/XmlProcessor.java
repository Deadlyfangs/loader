package com.banzai.fileloader.processor;

import com.banzai.fileloader.Entity.external.ContentXml;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class XmlProcessor {

    private final JAXBContext context;
    private final Schema schema;

    public File marshal(ContentXml contentXml) throws JAXBException {
        File content = new File(createFileName(contentXml));
        Marshaller marshaller = context.createMarshaller();

        marshaller.marshal(contentXml, content);

        return content;
    }

    public ContentXml unmarshal(File content) throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);
        unmarshaller.setEventHandler(new XmlValidationEventHandler());

        ContentXml contentXml = (ContentXml) unmarshaller.unmarshal(content);

        return contentXml;
    }

    private String createFileName(ContentXml contentXml) {
        StringBuilder sb = new StringBuilder();
        sb.append(contentXml.getCreationDate());
        sb.append("_processed");

        return sb.toString();
    }

}
