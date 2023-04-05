package gettingstarted;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig.config;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GettingStartedTest {

    @Autowired
    private FileRepository fileRepo;
    @Autowired
    private FileContentStore fileContentStore;

    @Autowired
    private WebApplicationContext context;

    private File file;

    {
        Describe("File Tests", () -> {
            BeforeEach(() -> {
                RestAssuredMockMvc.webAppContextSetup(context);
            });

            Context("given a file", () -> {
                BeforeEach(() -> {
                    file = new File();
                    file.setContentMimeType("text/plain");
                    file.setSummary("test file summary");
                    file = fileRepo.save(file);
                    int i=0;
                });

                It("should be possible for paul as an author to associate content", () -> {
                    given()
                        .auth().with(SecurityMockMvcRequestPostProcessors.user("paul").password("warren").authorities(new SimpleGrantedAuthority("ROLE_AUTHOR")))
                        .header("content-type", "text/plain")
                        .body("Hello Spring Content with RBAC")
                    .when()
                        .put("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_CREATED);
                });

                It("should not be possible for eric as a reader to associate content", () -> {
                    given()
                        .auth().with(SecurityMockMvcRequestPostProcessors.user("eric").password("wimp").authorities(new SimpleGrantedAuthority("ROLE_READER")))
                        .header("content-type", "text/plain")
                        .body("Hello Spring Content with RBAC")
                    .when()
                        .put("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_FORBIDDEN);
                });

                It("should be possible for paul as an author to remove content", () -> {
                    given()
                        .auth().with(SecurityMockMvcRequestPostProcessors.user("paul").password("warren").authorities(new SimpleGrantedAuthority("ROLE_AUTHOR")))
                        .header("content-type", "text/plain")
                        .body("Hello Spring Content with RBAC")
                    .when()
                        .put("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_CREATED);

                    given()
                        .auth().with(SecurityMockMvcRequestPostProcessors.user("paul").password("warren").authorities(new SimpleGrantedAuthority("ROLE_AUTHOR")))
                        .header("accept", "text/plain")
                    .when()
                        .delete("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_NO_CONTENT);
                });

                It("should not be possible for eric as a reader to remove content", () -> {
                    given()
                        .auth().with(SecurityMockMvcRequestPostProcessors.user("paul").password("warren").authorities(new SimpleGrantedAuthority("ROLE_AUTHOR")))
                        .header("content-type", "text/plain")
                        .body("Hello Spring Content with RBAC")
                    .when()
                        .put("/files/" + file.getId() + "/content")
                    .then()
                        .statusCode(HttpStatus.SC_CREATED);

                    given()
                        .auth().with(SecurityMockMvcRequestPostProcessors.user("eric").password("wimp").authorities(new SimpleGrantedAuthority("ROLE_READER")))
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
