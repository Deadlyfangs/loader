package com.banzai.fileloader.Entity.external;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;


@XmlRootElement(name = "Entry")
public class ContentXml {

    private long id;
    private String content;
    private LocalDateTime creationDate;

    public ContentXml() {
    }

    public ContentXml(long id, String content, LocalDateTime creationDate) {
        this.id = id;
        this.content = content;
        this.creationDate = creationDate;
    }

    @XmlElement
    public void setContent(String content) {
        this.content = content;
    }

    @XmlElement
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

}
