package com.emc.spring.content.gs.mongorest;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.emc.spring.content.gs.mongorest.ContentMetadataStore;
import com.emc.spring.content.gs.mongorest.SpringDocument;
import com.emc.spring.content.gs.mongorest.SpringDocumentRepository;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = com.emc.spring.content.gs.mongorest.ContentApplication.class)
@WebAppConfiguration   
@IntegrationTest("server.port:0")  
public class SpringDocumentTests {

	@Autowired
	private SpringDocumentRepository springDocRepo;
	
	@Autowired
	private ContentMetadataStore claimFormStore;
	
    @Value("${local.server.port}")   // 6
    int port;

    private SpringDocument canSetSpringDoc;
    private SpringDocument canGetSpringDoc;
    private SpringDocument canDelSpringDoc;
    
    @Before
    public void setUp() throws Exception {
    	
        RestAssured.port = port;
    	
		// delete any existing claim forms
		Iterable<SpringDocument> existingClaims = springDocRepo.findAll();
		for (SpringDocument existingClaim : existingClaims) {
			claimFormStore.unsetContent(existingClaim.getContent());
		}
		
    	// ensure clean state
    	springDocRepo.deleteAll();

    	// create a claim that can set content on
    	canSetSpringDoc = new SpringDocument();
    	canSetSpringDoc.setTitle("A SpringDoc with no content");
    	canSetSpringDoc.setKeywords(Collections.singletonList("keyword"));
    	springDocRepo.save(canSetSpringDoc);

    	// create a claim that can get content from
    	canGetSpringDoc = new SpringDocument();
    	canGetSpringDoc.setTitle("A SpringDoc with hello world content");
    	canGetSpringDoc.setKeywords(new ArrayList<String>());
    	canGetSpringDoc.getKeywords().add("hello");
    	canGetSpringDoc.getKeywords().add("world");
    	springDocRepo.save(canGetSpringDoc);
    	canGetSpringDoc.setContent(new SpringDocument.ContentMetadata());
    	canGetSpringDoc.getContent().setMimeType("plain/text");
    	claimFormStore.setContent(canGetSpringDoc.getContent(), new ByteArrayInputStream("Hello Spring Content World!".getBytes()));
    	springDocRepo.save(canGetSpringDoc);

    	// create a doc that can delete content from
    	canDelSpringDoc = new SpringDocument();
    	canDelSpringDoc.setTitle("A SpringDoc that can be deleted");
    	canDelSpringDoc.setKeywords(new ArrayList<String>());
    	canDelSpringDoc.getKeywords().add("hello");
    	canDelSpringDoc.getKeywords().add("world");
    	springDocRepo.save(canDelSpringDoc);
    	canDelSpringDoc.setContent(new SpringDocument.ContentMetadata());
    	canDelSpringDoc.getContent().setMimeType("plain/text");
    	claimFormStore.setContent(canDelSpringDoc.getContent(), new ByteArrayInputStream("This is plain text content!".getBytes()));
    	springDocRepo.save(canDelSpringDoc);
    }

    @Test
    public void canSetContent() {
    	JsonPath response = 
    	given()
			.contentType("plain/text")
			.content("We set this content!".getBytes())
	    .when()
	        .post("/docs/" + canSetSpringDoc.getId() + "/content")
	    .then()
	    	.statusCode(HttpStatus.SC_CREATED)
	    	.extract()
	    		.jsonPath();
    	
    	Assert.assertNotNull(response.get("_links"));
    	Assert.assertNotNull(response.get("_links.self"));
    	Assert.assertNotNull(response.get("_links.self.href"));
    }

    @Test
    public void canGetContent() {
    	JsonPath response = 
		    when()
		        .get("/docs/" + canGetSpringDoc.getId())
		    .then()
		    	.statusCode(HttpStatus.SC_OK)
		    	.extract()
		    		.jsonPath();
    	
    	Assert.assertNotNull(response.get("_links.content"));
    	Assert.assertNotNull(response.get("_links.content.href"));

    	String contentUrl = response.get("_links.content.href");
    	when()
    		.get(contentUrl)
    	.then()
    		.assertThat()
    			.contentType(Matchers.startsWith("plain/text"))
    			.body(Matchers.equalTo("Hello Spring Content World!"));
    }

    @Test
    public void canDeleteContent() {
    	JsonPath response = 
		    when()
		        .get("/docs/" + canDelSpringDoc.getId())
		    .then()
		    	.statusCode(HttpStatus.SC_OK)
		    	.extract()
		    		.jsonPath();
    	
    	Assert.assertNotNull(response.get("_links.content"));
    	Assert.assertNotNull(response.get("_links.content.href"));

    	String contentUrl = response.get("_links.content.href");
    	when()
    		.delete(contentUrl)
    	.then()
    		.assertThat()
    			.statusCode(HttpStatus.SC_NO_CONTENT);

    	// and make sure that it is really gone
    	when()
    		.get(contentUrl)
    	.then()
    		.assertThat()
    			.statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
