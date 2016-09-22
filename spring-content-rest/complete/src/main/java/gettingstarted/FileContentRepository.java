package gettingstarted;

import org.springframework.content.commons.repository.ContentStore;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

@ContentStoreRestResource
public interface FileContentRepository extends ContentStore<File, String> {
}
