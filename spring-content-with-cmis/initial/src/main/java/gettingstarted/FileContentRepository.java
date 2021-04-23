package gettingstarted;

import org.springframework.content.commons.repository.ContentStore;

public interface FileContentRepository extends ContentStore<File, String> {
}
