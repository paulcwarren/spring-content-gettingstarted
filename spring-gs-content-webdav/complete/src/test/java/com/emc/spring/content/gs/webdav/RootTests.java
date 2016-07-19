package com.emc.spring.content.gs.webdav;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.function.Predicate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.jayway.restassured.RestAssured;

import spring.content.gs.webdav.Application;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration   
@IntegrationTest("server.port:0")  
public class RootTests {

    @Value("${local.server.port}")
    private int port;
    private Sardine sardine;
    private int id;
    {
    	Describe("Root", () -> {
    		BeforeEach(() -> {
    			id = System.identityHashCode(this);
    	        RestAssured.port = port;
				sardine = SardineFactory.begin("user", "password");
    		});
    		It("should contain nothing", () -> {
    			List<DavResource> resources = sardine.list(getUrl("/"));
    			resources.removeIf(new Predicate<DavResource>() {
					@Override
					public boolean test(DavResource t) {
						return t.getDisplayName().equals("spring-webdav") || 
							   t.getDisplayName().equals(".DS_Store");
					}
    			});
    			assertThat(resources.size(), is(0));
    		});
    	});
    }
	
	@Test
	public void contextLoads() {
	}
	
	protected String getUrl(String path) {
		return "http://localhost:" + port + path;
	}
}
