package com.banzai.fileloader.processor;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

@Slf4j
public class XmlValidationEventHandler implements ValidationEventHandler {

    @Override
    public boolean handleEvent(ValidationEvent event) {
        log.info("\nEVENT");
        log.info("SEVERITY: {}", event.getSeverity());
        log.info("MESSAGE: {}", event.getMessage());
        log.info("LINKED EXCEPTION: {}", event.getLinkedException());
        log.info("LOCATOR");
        log.info("    LINE NUMBER: {}", event.getLocator().getLineNumber());
        log.info("    COLUMN NUMBER: {}", event.getLocator().getColumnNumber());
        log.info("    OFFSET: {}", event.getLocator().getOffset());
        log.info("    OBJECT: {}", event.getLocator().getObject());
        log.info("    NODE: {}", event.getLocator().getNode());
        log.info("    URL: {}", event.getLocator().getURL());

        if(event.getSeverity() == ValidationEvent.ERROR
                ||event.getSeverity() == ValidationEvent.FATAL_ERROR) {
            return false;
        }

        return true;
    }

}
