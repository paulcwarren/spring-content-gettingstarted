package gettingstarted;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.commons.annotations.HandleAfterSetContent;
import org.springframework.content.commons.annotations.HandleBeforeUnsetContent;
import org.springframework.content.commons.annotations.StoreEventHandler;
import org.springframework.content.commons.io.DeletableResource;
import org.springframework.content.commons.property.PropertyPath;
import org.springframework.content.commons.renditions.RenditionService;
import org.springframework.content.commons.repository.events.AfterSetContentEvent;
import org.springframework.content.commons.repository.events.BeforeUnsetContentEvent;
import org.springframework.core.io.WritableResource;

@StoreEventHandler
public class StoredRenditionsEventHandler {

    @Autowired
    private FileRepository repo;

    @Autowired
    private FileContentStore store;

    @Autowired
    private RenditionService renditionService;

    @HandleAfterSetContent
    public void onAfterSetContent(AfterSetContentEvent event)
            throws IOException {

        File entity = (File)event.getSource();

        long renderedLength = 0;



        try (InputStream originalInputStream = store.getContent(entity, PropertyPath.from("content"))) {

            InputStream renderedContent = renditionService.convert("text/plain", originalInputStream, "image/jpeg");

            try (OutputStream renditionPropertyStream = ((WritableResource)store.getResource(entity, PropertyPath.from("rendition"))).getOutputStream()) {
                renderedLength = IOUtils.copyLarge(
                        renderedContent,
                        renditionPropertyStream);
            }
        }

        entity.setRenditionLen(renderedLength);
        entity.setRenditionMimeType("image/jpeg");
    }

    @HandleBeforeUnsetContent
    public void onBeforeUnsetContent(BeforeUnsetContentEvent event) throws IOException {

        File entity = (File)event.getSource();

        ((DeletableResource)store.getResource(entity, PropertyPath.from("rendition"))).delete();

        entity.setRenditionId(null);
        entity.setRenditionLen(0L);
        entity.setRenditionMimeType(null);
    }
}
