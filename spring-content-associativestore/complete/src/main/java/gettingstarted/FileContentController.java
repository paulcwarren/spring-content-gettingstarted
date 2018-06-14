package gettingstarted;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
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
	@Autowired private FileStore contentStore;
	
	@RequestMapping(value="/files/{fileId}", method = RequestMethod.POST)
	public ResponseEntity<?> setContent(@PathVariable("fileId") Long id, @RequestParam("file") MultipartFile file) 
			throws IOException {

		Optional<File> f = filesRepo.findById(id);
		if (f.isPresent()) {

			f.get().setMimeType(file.getContentType());

			String originalFilename = file.getOriginalFilename();
			InputStream is = file.getInputStream();
			OutputStream os = ((WritableResource)contentStore.getResource(originalFilename)).getOutputStream();

			IOUtils.copyLarge(is, os);

			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);

			// associate content
			contentStore.associate(f.get(), originalFilename);

			// save updated content-related info
			filesRepo.save(f.get());

			return new ResponseEntity<Object>(HttpStatus.OK);
		}
		return null;
	}

	@RequestMapping(value="/files/{fileId}", method = RequestMethod.GET)
	public ResponseEntity<?> getContent(@PathVariable("fileId") Long id) throws IOException {

		Optional<File> f = filesRepo.findById(id);
		if (f.isPresent()) {
			Resource r = contentStore.getResource(f.get().getContentId());
			HttpHeaders headers = new HttpHeaders();
			headers.setContentLength(r.contentLength());
			headers.set("Content-Type", f.get().getMimeType());
			return new ResponseEntity<Object>(r, headers, HttpStatus.OK);
		}
		return null;
	}
}