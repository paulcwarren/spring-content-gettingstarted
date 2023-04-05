package gettingstarted;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.config;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

import java.io.ByteArrayInputStream;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import org.springframework.web.context.WebApplicationContext;


@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GettingStartedTest {

	@Autowired private FileRepository fileRepo;
	@Autowired private FileContentStore fileContentStore;

	@Autowired
	private WebApplicationContext context;

    private File file1;

    {
        Describe("Rendition Tests", () -> {
        	BeforeEach(() -> {
				RestAssuredMockMvc.webAppContextSetup(context);
        	});

        	Context("given a File Entity with content", () -> {
        		BeforeEach(() -> {
        			file1 = new File();
					file1.setContentMimeType("text/plain");
					file1 = fileContentStore.setContent(file1, new ByteArrayInputStream("Hello Spring World!".getBytes()));
					file1 = fileRepo.save(file1);
        		});

        		It("should be renderable", () -> {
					given()
						.config(config()
								.encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
						.header("accept", "image/jpeg")
						.get("/files/" + file1.getId() + "/content")
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
