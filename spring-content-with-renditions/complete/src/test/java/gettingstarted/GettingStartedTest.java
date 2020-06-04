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

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.CoreMatchers.*;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GettingStartedTest {

	@Autowired private FileRepository fileRepo;
	@Autowired private FileContentStore fileContentStore;
	
    @Value("${local.server.port}") private int port;

    private File file1;
    
    {
        Describe("Rendition Tests", () -> {
        	BeforeEach(() -> {
        		RestAssured.port = port;
        	});
        	
        	Context("given a File Entity with content", () -> {
        		BeforeEach(() -> {
        			file1 = new File();
					file1.setMimeType("text/plain");
					file1 = fileContentStore.setContent(file1, new ByteArrayInputStream("Hello Spring World!".getBytes()));
					file1 = fileRepo.save(file1);
        		});
        		
        		It("should be renderable", () -> {
					given()
						.config(RestAssured.config()
								.encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
						.header("accept", "image/jpeg")
						.get("/files/" + file1.getId())
						.then()
						.statusCode(HttpStatus.SC_OK)
						.assertThat().body(is(not(nullValue())));
				});
        	});
        });
    }

    @Test
    public void noop() {}
}
