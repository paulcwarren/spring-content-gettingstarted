package gettingstarted;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GettingStartedTest {

	@Autowired private FileRepository fileRepo;
	@Autowired private FileStore fileStore;
	
    @Value("${local.server.port}") private int port;

    private File file;
    
    {
        Describe("File Tests", () -> {
        	BeforeEach(() -> {
        		RestAssured.port = port;
        	});
        	
        	Context("Given a File Entity", () -> {
        		BeforeEach(() -> {
            		File f = new File();
            		f.setName("test-file");
            		f.setMimeType("text/plain");
            		f.setSummary("test file summary");
            		file = fileRepo.save(f);
        		});
        		
        		It("should be able to associate content with the Entity", () -> {
        			Long fid = file.getId();

        	    	given()
        	    		.multiPart("file", "file", new ByteArrayInputStream("This is plain text content!".getBytes()), "text/plain")
        		    .when()
        		        .post("/files/" + fid)
        		    .then()
        		    	.statusCode(HttpStatus.SC_OK);
                	    	
        	    	Optional<File> file = fileRepo.findById(fid);

					Resource r = fileStore.getResource(file.get());
					InputStream is = r.getInputStream();
        	    	assertThat(IOUtils.toString(is), is("This is plain text content!"));
        	    	IOUtils.closeQuietly(is);
        		});
        		
        		Context("with existing content", () -> {
        			BeforeEach(() -> {
						InputStream is = new ByteArrayInputStream("Existing content".getBytes());
						OutputStream os = ((WritableResource)fileStore.getResource("existing-content")).getOutputStream();
						IOUtils.copy(is, os);
						IOUtils.closeQuietly(is);
						IOUtils.closeQuietly(os);
						fileStore.associate(file, "existing-content");
        				fileRepo.save(file);
        			});
        			
        			It("should return the content", () -> {
        		    	given()
        		    		.header("accept", "text/plain")
        		    	.when()
        	    			.get("files/" + file.getId())
        	    		.then()
	        	    		.assertThat()
	        	    			.contentType(Matchers.startsWith("text/plain"))
	        	    			.body(Matchers.equalTo("Existing content"));
        			});
        		});
        	});
        });
    }

    @Test
    public void noop() {}
}
