package gettingstarted;

import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.search.Searchable;
import org.springframework.stereotype.Component;

@Component  // just to keep the ide happy!
public interface FileContentStore extends ContentStore<File, String>, Searchable<String> {
}
