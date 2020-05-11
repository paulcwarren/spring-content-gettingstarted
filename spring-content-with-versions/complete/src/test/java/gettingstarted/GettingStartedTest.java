package gettingstarted;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.util.List;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GettingStartedTest {

	@Autowired private FileRepository fileRepo;
	@Autowired private FileContentStore fileContentStore;
	
    @Value("${local.server.port}") private int port;

    private File file;
    
    {
        Describe("File Tests", () -> {
        	BeforeEach(() -> {
        		RestAssured.port = port;
        	});
        	
        	Context("Given a File Entity", () -> {
        		BeforeEach(() -> {
            		file = new File();
            		file.setMimeType("text/plain");
            		file.setSummary("test file summary");
            		file = fileContentStore.setContent(file, new ByteArrayInputStream("Hello Spring Content World!".getBytes()));
					file = fileRepo.save(file);
        		});
        		
        		It("should be versionable", () -> {
					given()
							.config(RestAssured.config()
									.encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
							.header("content-type", "application/hal+json")
							.put("/files/" + file.getId() + "/lock")
							.then()
							.statusCode(HttpStatus.SC_OK);

					given()
							.config(RestAssured.config()
									.encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
							.contentType("application/json")
							.content("{\"number\":\"1.1\",\"label\":\"some minor changes\"}".getBytes())
							.put("/files/" + file.getId() + "/version")
							.then()
							.statusCode(HttpStatus.SC_OK);

					List<File> versionedFile = fileRepo.findAllVersionsLatest(File.class);
					assertThat(versionedFile.size(), is(1));
					assertThat(versionedFile.get(0).getVersion(), is("1.1"));
					assertThat(versionedFile.get(0).getLabel(), is("some minor changes"));

					given()
							.config(RestAssured.config()
									.encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
							.header("accept", "application/hal+json")
							.delete("/files/" + versionedFile.get(0).getId() + "/lock")
							.then()
							.statusCode(HttpStatus.SC_OK);
				});
        	});
        });
    }

    @Test
    public void noop() {}
}
