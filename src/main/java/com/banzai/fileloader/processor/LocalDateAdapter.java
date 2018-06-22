package com.banzai.fileloader.processor;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return LocalDateTime.parse(v, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return null;
    }
}
