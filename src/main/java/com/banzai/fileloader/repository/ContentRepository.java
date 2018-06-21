package com.banzai.fileloader.repository;


import com.banzai.fileloader.Entity.internal.ContentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends CrudRepository<ContentEntity, Long> {

}
