package com.banzai.fileloader.entity.internal;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "contents")
@Getter
@Setter
@NoArgsConstructor
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String content;
    LocalDateTime creationDate;

}
