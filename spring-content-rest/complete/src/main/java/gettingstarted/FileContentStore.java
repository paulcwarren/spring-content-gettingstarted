package gettingstarted;

import org.springframework.content.commons.repository.ContentStore;

import org.springframework.content.rest.StoreRestResource;

@StoreRestResource
public interface FileContentStore extends ContentStore<File, String> {
}
