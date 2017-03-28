package gettingstarted;

import org.springframework.content.commons.repository.ContentStore;

import internal.org.springframework.content.rest.annotations.ContentStoreRestResource;

@ContentStoreRestResource
public interface FileContentStore extends ContentStore<File, String> {
}
