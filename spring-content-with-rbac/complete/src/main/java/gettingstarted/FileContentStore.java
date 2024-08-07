package gettingstarted;

import org.springframework.content.commons.property.PropertyPath;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.repository.UnsetContentParams;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Component  // just to keep the ide happy!
public interface FileContentStore extends ContentStore<File, String> {

	@Override
	@PreAuthorize("hasRole('ROLE_AUTHOR')")
	public File unsetContent(File file, PropertyPath propertyPath, UnsetContentParams params);

}
