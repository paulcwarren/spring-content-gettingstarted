package gettingstarted;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GettingStartedTest {

	@Autowired private FileRepository fileRepo;
	@Autowired private FileContentStore fileContentStore;

	@Autowired
	private WebApplicationContext context;


    private File file;

    {
        Describe("File Tests", () -> {
        	BeforeEach(() -> {
				RestAssuredMockMvc.webAppContextSetup(context);
        	});

        	Context("Given a File Entity", () -> {
        		BeforeEach(() -> {
            		File f = new File();
            		f.setName("test-file");
            		f.setContentMimeType("text/plain");
            		f.setSummary("test file summary");
            		file = fileRepo.save(f);
        		});

        		It("should be able to associate content with the Entity", () -> {
        			Long fid = file.getId();

        	    	given()
        	    		.multiPart("file", "file", new ByteArrayInputStream("This is plain text content!".getBytes()), "text/plain")
        		    .when()
        		        .put("/files/" + fid + "/content")
        		    .then()
        		    	.statusCode(HttpStatus.SC_CREATED);

        	    	Optional<File> file = fileRepo.findById(fid);
        	    	assertThat(IOUtils.toString(fileContentStore.getContent(file.get())), is("This is plain text content!"));
        		});

        		Context("with existing content", () -> {
        			BeforeEach(() -> {
        				fileContentStore.setContent(file, new ByteArrayInputStream("Existing content".getBytes()));
        				fileRepo.save(file);
        			});

        			It("should return the content", () -> {
        		    	given()
        		    		.header("accept", "text/plain")
        		    	.when()
        	    			.get("files/" + file.getId() + "/content")
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
