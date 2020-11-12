package gettingstarted;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.jayway.restassured.RestAssured;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GettingStartedTest {

    @Autowired
    private FileRepository fileRepo;
    @Autowired
    private FileContentStore fileContentStore;

    @Value("${local.server.port}")
    private int port;

    private File file;

    {
        Describe("File Tests", () -> {
            BeforeEach(() -> {
                RestAssured.port = port;
            });

            Context("given a file", () -> {
                BeforeEach(() -> {
                    file = new File();
                    file.setMimeType("text/plain");
                    file.setSummary("test file summary");
                    file = fileRepo.save(file);
                });

                It("should be possible for paul as an author to associate content", () -> {
                    given()
                        .config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                        .auth().preemptive().basic("paul", "warren")
                        .header("content-type", "text/plain")
                        .body("Hello Spring Content with RBAC")
                    .when()
                        .put("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_CREATED);
                });

                It("should not be possible for eric as a reader to associate content", () -> {
                    given()
                        .config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                        .auth().preemptive().basic("eric", "wimp")
                        .header("content-type", "text/plain")
                        .body("Hello Spring Content with RBAC")
                    .when()
                        .put("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_FORBIDDEN);
                });

                It("should be possible for paul as an author to remove content", () -> {
                    given()
                        .config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                        .auth().preemptive().basic("paul", "warren")
                        .header("content-type", "text/plain")
                        .body("Hello Spring Content with RBAC")
                    .when()
                        .put("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_CREATED);

                    given()
                        .config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                        .auth().preemptive().basic("paul", "warren")
                        .header("accept", "text/plain")
                    .when()
                        .delete("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_NO_CONTENT);
                });

                It("should not be possible for eric as a reader to remove content", () -> {
                    given()
                        .config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                        .auth().preemptive().basic("paul", "warren")
                        .header("content-type", "text/plain")
                        .body("Hello Spring Content with RBAC")
                    .when()
                        .put("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_CREATED);

                    given()
                        .config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                        .auth().preemptive().basic("eric", "wimp")
                        .header("accept", "text/plain")
                    .when()
                        .delete("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_FORBIDDEN);
                });
            });
        });
    }

    @Test
    public void noop() {
    }
}
