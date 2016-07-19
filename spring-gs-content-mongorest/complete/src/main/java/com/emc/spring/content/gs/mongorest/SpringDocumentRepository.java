package com.emc.spring.content.gs.mongorest;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="/docs", collectionResourceRel="docs")
public interface SpringDocumentRepository extends CrudRepository<SpringDocument, String> {

}
