package com.banzai.fileloader.parser;

import com.banzai.fileloader.entity.external.ContentXml;
import com.banzai.fileloader.exception.XmlFormatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import java.io.File;

@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class XmlProcessor {

    private final JAXBContext context;
    private final Schema schema;

    @Deprecated
    public File marshal(ContentXml contentXml) throws JAXBException {
        File content = new File(createFileName(contentXml));
        Marshaller marshaller = context.createMarshaller();

        marshaller.marshal(contentXml, content);

        return content;
    }

    public ContentXml unmarshal(File content) throws XmlFormatException {
        try {
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(new XmlValidationEventHandler());

            ContentXml contentXml = (ContentXml) unmarshaller.unmarshal(content);

            return contentXml;

        } catch (JAXBException e) {
            throw new XmlFormatException("Invalid XML format.");
        }
    }

    @Deprecated
    private String createFileName(ContentXml contentXml) {
        StringBuilder sb = new StringBuilder();
        sb.append(contentXml.getCreationDate());
        sb.append("_processed");

        return sb.toString();
    }

}
