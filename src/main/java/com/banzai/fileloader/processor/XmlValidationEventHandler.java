package com.banzai.fileloader.processor;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

@Slf4j
public class XmlValidationEventHandler implements ValidationEventHandler {

    @Override
    public boolean handleEvent(ValidationEvent event) {
        log.error("*** XML VALIDATION EVENT ***");
        log.error("SEVERITY: {}", event.getSeverity());
        log.error("MESSAGE: {}", event.getMessage());
//        log.error("LINKED EXCEPTION: {}", event.getLinkedException());
        log.error("LOCATOR");
        log.error("    LINE NUMBER: {}", event.getLocator().getLineNumber());
        log.error("    COLUMN NUMBER: {}", event.getLocator().getColumnNumber());
        log.error("    OFFSET: {}", event.getLocator().getOffset());
        log.error("    OBJECT: {}", event.getLocator().getObject());
        log.error("    NODE: {}", event.getLocator().getNode());
        log.error("    URL: {}", event.getLocator().getURL());

        if(event.getSeverity() == ValidationEvent.ERROR
                ||event.getSeverity() == ValidationEvent.FATAL_ERROR) {
            return false;
        }

        return true;
    }

}
