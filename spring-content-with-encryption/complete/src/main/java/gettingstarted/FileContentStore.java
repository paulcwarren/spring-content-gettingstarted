package gettingstarted;

import org.springframework.content.commons.repository.ContentStore;

import org.springframework.content.encryption.EncryptingContentStore;
import org.springframework.content.rest.StoreRestResource;

@StoreRestResource
public interface FileContentStore extends ContentStore<File, String>, EncryptingContentStore<File, String> {
}
