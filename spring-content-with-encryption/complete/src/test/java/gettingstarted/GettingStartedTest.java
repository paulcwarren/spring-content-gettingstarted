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
import org.springframework.content.encryption.EnvelopeEncryptionService;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.vault.core.VaultOperations;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.util.Optional;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.*;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GettingStartedTest {

	@Autowired private FileRepository repo;
	@Autowired private FileContentStore store;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private FileSystemResourceLoader storeLoader;

	@Autowired
	private EnvelopeEncryptionService encrypter;

	@Autowired
	private VaultOperations vaultOperations;

	private File f;

	{
		Describe("Client-side encryption with fs storage", () -> {
			BeforeEach(() -> {
				RestAssuredMockMvc.webAppContextSetup(context);

				f = repo.save(new File());
			});
			Context("given content", () -> {
				BeforeEach(() -> {
					given()
							.contentType("text/plain")
							.body("Hello Client-side encryption World!")
							.when()
							.post("/files/" + f.getId() + "/content")
							.then()
							.statusCode(HttpStatus.SC_CREATED);
				});
				It("should be stored encrypted", () -> {
					Optional<File> fetched = repo.findById(f.getId());
					assertThat(fetched.isPresent(), is(true));
					f = fetched.get();

					String contents = IOUtils.toString(new FileInputStream(new java.io.File(storeLoader.getFilesystemRoot(), f.getContentId().toString())));
					assertThat(contents, is(not("Hello Client-side encryption World!")));
				});
				It("should be retrieved decrypted", () -> {
					given()
							.header("accept", "text/plain")
							.get("/files/" + f.getId() + "/content")
							.then()
							.statusCode(HttpStatus.SC_OK)
							.assertThat()
							.contentType(Matchers.startsWith("text/plain"))
							.body(Matchers.equalTo("Hello Client-side encryption World!"));
				});
				It("it should remove the content and clear the content key when unset", () -> {
					f = repo.findById(f.getId()).get();
					String contentId = f.getContentId().toString();

					given()
							.delete("/files/" + f.getId() + "/content")
							.then()
							.statusCode(HttpStatus.SC_NO_CONTENT);

					f = repo.findById(f.getId()).get();
					assertThat(f.getContentKey(), is(nullValue()));
					assertThat(new java.io.File(storeLoader.getFilesystemRoot(), contentId).exists(), is(false));
				});
			});
		});
	}

	@Test
	public void noop() {}
}
