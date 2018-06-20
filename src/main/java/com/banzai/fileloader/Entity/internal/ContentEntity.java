package com.banzai.fileloader.Entity.internal;


import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "contents")
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String content;
    LocalDateTime creationDate;

}
