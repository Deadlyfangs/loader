package com.banzai.fileloader.Entity.external;


import com.banzai.fileloader.parser.LocalDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;


@XmlRootElement(name = "Entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContentXml {
    @XmlElement
    private String content;

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDateTime creationDate;

    public ContentXml() {
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

}
