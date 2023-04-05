package gettingstarted;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.web.context.WebApplicationContext;

import java.io.ByteArrayInputStream;
import java.util.List;

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
            		file = new File();
            		file.setContentMimeType("text/plain");
            		file.setSummary("test file summary");
            		file = fileContentStore.setContent(file, new ByteArrayInputStream("Hello Spring Content World!".getBytes()));
					file = fileRepo.save(file);
        		});

        		It("should be versionable", () -> {
					given()
							.auth().with(SecurityMockMvcRequestPostProcessors.user("paul").password("warren"))
							.header("content-type", "application/hal+json")
							.put("/files/" + file.getId() + "/lock")
							.then()
							.statusCode(HttpStatus.SC_OK);

					given()
							.auth().with(SecurityMockMvcRequestPostProcessors.user("paul").password("warren"))
							.contentType("application/hal+json")
							.body("{\"number\":\"1.1\",\"label\":\"some minor changes\"}".getBytes())
							.put("/files/" + file.getId() + "/version")
							.then()
							.statusCode(HttpStatus.SC_OK);

					List<File> versionedFile = fileRepo.findAllVersionsLatest(File.class);
					assertThat(versionedFile.size(), is(1));
					assertThat(versionedFile.get(0).getVersion(), is("1.1"));
					assertThat(versionedFile.get(0).getLabel(), is("some minor changes"));
					assertThat(versionedFile.get(0).getContentId(), is(file.getContentId()));
					assertThat(versionedFile.get(0).getContentLength(), is(file.getContentLength()));
					assertThat(versionedFile.get(0).getContentMimeType(), is(file.getContentMimeType()));

					given()
							.auth().with(SecurityMockMvcRequestPostProcessors.user("paul").password("warren"))
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
