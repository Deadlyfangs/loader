package com.example.file.processor.repository;


import com.example.file.processor.entity.internal.ContentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends CrudRepository<ContentEntity, Long> {

}
