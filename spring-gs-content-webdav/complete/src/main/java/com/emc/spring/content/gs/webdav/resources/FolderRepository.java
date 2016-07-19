package com.emc.spring.content.gs.webdav.resources;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FolderRepository extends MongoRepository<Folder, String> {
	
	List<Folder> findAllByParent(Folder parent);

	List<Folder> findAllByParentIdIsNull();

}
