package gettingstarted;

import org.springframework.content.commons.renditions.Renderable;
import org.springframework.content.commons.store.ContentStore;
import org.springframework.stereotype.Component;

@Component  // just to keep the ide happy!
public interface FileContentStore extends ContentStore<File, String>, Renderable<File> {
}
