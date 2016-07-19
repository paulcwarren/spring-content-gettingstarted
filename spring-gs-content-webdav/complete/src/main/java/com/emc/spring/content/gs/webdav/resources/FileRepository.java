package com.emc.spring.content.gs.webdav.resources;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<File, String> {

	List<File> findAllByParent(Folder parent);
	
	List<File> findAllByParentIsNull();
}
