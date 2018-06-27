//package com.banzai.fileloader.parser;
//
//
//import com.banzai.fileloader.entity.external.ContentXml;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.stereotype.Component;
//import org.xml.sax.SAXException;
//
//import javax.xml.XMLConstants;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.validation.Schema;
//import javax.xml.validation.SchemaFactory;
//
//@Component
//@Getter
//@Setter
//public class JaxbContextLoader {
//
//    private JAXBContext jaxbContext;
//    private Schema schema;
//
//    public JaxbContextLoader() throws SAXException, JAXBException {
//        jaxbContext = JAXBContext.newInstance(ContentXml.class);
//        schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).
//                newSchema(getClass().getClassLoader().getResource("xml/content_schema.xsd"));
//    }
//}
