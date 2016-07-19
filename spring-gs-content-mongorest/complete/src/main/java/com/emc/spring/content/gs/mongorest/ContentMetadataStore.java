package com.emc.spring.content.gs.mongorest;

import com.emc.spring.content.commons.repository.ContentStore;
import com.emc.spring.content.gs.mongorest.SpringDocument.ContentMetadata;

import internal.com.emc.spring.content.rest.annotations.ContentStoreRestResource;

@ContentStoreRestResource
public interface ContentMetadataStore extends ContentStore<ContentMetadata, String> {

}
