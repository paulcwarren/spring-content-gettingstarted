package gettingstarted;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileContentController {

	@Autowired private FileRepository filesRepo;
	@Autowired private FileContentStore contentStore;
	
	@RequestMapping(value="/files/{fileId}", method = RequestMethod.PUT)
	public ResponseEntity<?> setContent(@PathVariable("fileId") Long id, @RequestParam("file") MultipartFile file) 
			throws IOException {

		Optional<File> f = filesRepo.findById(id);
		if (f.isPresent()) {
			f.get().setMimeType(file.getContentType());

			contentStore.setContent(f.get(), file.getInputStream());

			// save updated content-related info
			filesRepo.save(f.get());

			return new ResponseEntity<Object>(HttpStatus.OK);
		}
		return null;
	}

	@RequestMapping(value="/files/{fileId}", method = RequestMethod.GET)
	public ResponseEntity<?> getContent(@PathVariable("fileId") Long id) {

		Optional<File> f = filesRepo.findById(id);
		if (f.isPresent()) {
			InputStreamResource inputStreamResource = new InputStreamResource(contentStore.getContent(f.get()));
			HttpHeaders headers = new HttpHeaders();
			headers.setContentLength(f.get().getContentLength());
			headers.set("Content-Type", f.get().getMimeType());
			return new ResponseEntity<Object>(inputStreamResource, headers, HttpStatus.OK);
		}
		return null;
	}
}