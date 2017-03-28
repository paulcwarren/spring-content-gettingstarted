package gettingstarted;

import java.io.IOException;

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
	
	@RequestMapping(value="/files/{fileId}", method = RequestMethod.PUT, headers="content-type!=application/hal+json")
	public ResponseEntity<?> setContent(@PathVariable("fileId") Long id, @RequestParam("file") MultipartFile file) 
			throws IOException {

		File f = filesRepo.findOne(id);
		f.setMimeType(file.getContentType());
		
		contentStore.setContent(f, file.getInputStream());
		
		// save updated content-related info
		filesRepo.save(f);
			
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@RequestMapping(value="/files/{fileId}", method = RequestMethod.GET, headers="accept!=application/hal+json")
	public ResponseEntity<?> getContent(@PathVariable("fileId") Long id) {

		File f = filesRepo.findOne(id);
		InputStreamResource inputStreamResource = new InputStreamResource(contentStore.getContent(f));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(f.getContentLength());
		headers.set("Content-Type", 	f.getMimeType());
		return new ResponseEntity<Object>(inputStreamResource, headers, HttpStatus.OK);
	}
}