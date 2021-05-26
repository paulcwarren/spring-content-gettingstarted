package gettingstarted;

import static com.github.grantwest.eventually.EventuallyLambdaMatcher.eventuallyEval;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.time.Duration;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(classes= {SpringContentApplication.class, TestConfig.class}, webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GettingStartedTest {

	@Autowired private FileRepository fileRepo;
	@Autowired private FileContentStore fileContentStore;

    @Value("${local.server.port}") private int port;

    private File file1, file2;

    {
        Describe("Fulltext Tests", () -> {
        	BeforeEach(() -> {
        		RestAssured.port = port;
        	});

        	Context("given a File Entity with content", () -> {
        		BeforeEach(() -> {
        			file1 = new File();
					file1.setMimeType("text/plain");
					file1 = fileContentStore.setContent(file1, new ByteArrayInputStream("Hello Spring World!".getBytes()));
					file1 = fileRepo.save(file1);

            		file2 = new File();
            		file2.setMimeType("text/plain");
            		file2 = fileContentStore.setContent(file2, new ByteArrayInputStream("Hello Spring Content World!".getBytes()));
					file2 = fileRepo.save(file2);
        		});

        		It("should be searchable", () -> {

                    assertThat(() -> {
                        return given()
                                .config(RestAssured.config()
                                .encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                                .header("accept", "application/hal+json")
                            .get("/files/searchContent?queryString=Content")
                            .then()
                                .statusCode(HttpStatus.SC_OK)
                                .extract().body().asString();
                    },  eventuallyEval(allOf(
                                           not(containsString("/files/" + file1.getId())),
                                           containsString("/files/" + file2.getId())),
                                       Duration.ofSeconds(5)));
				});
        	});
        });
    }

    @Test
    public void noop() {}
}
