package com.emc.spring.content.gs.webdav;

import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.AfterEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.BeforeEach;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Context;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.Describe;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.It;
import static com.github.paulcwarren.ginkgo4j.Ginkgo4jDSL.FIt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.util.UriUtils;

import com.github.paulcwarren.ginkgo4j.Ginkgo4jSpringRunner;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.impl.SardineException;
import com.jayway.restassured.RestAssured;

import spring.content.gs.webdav.Application;

@RunWith(Ginkgo4jSpringRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration   
@IntegrationTest("server.port:0")  
public class FolderTests {

    @Value("${local.server.port}")
    private int port;
    private Sardine sardine;
    private int id;
    {
    	Describe("Folder", () -> {
    		BeforeEach(() -> {
    			id = System.identityHashCode(this);
    	        RestAssured.port = port;
				sardine = SardineFactory.begin("user", "password");
    		});
    		It("should be able to create a folder with spaces in the name", () -> {
				sardine.createDirectory(getUrl(String.format("/a folder with spaces in it %s", id)));
				sardine.delete(getUrl(String.format("/a folder with spaces in it %s", id)));
    		});
    		It("should be able to create a folder with spaces and other special chatacters in the name", () -> {
				sardine.createDirectory(getUrl(String.format("/(a folder with special characters in it %s)", id)));
				sardine.delete(getUrl(String.format("/(a folder with special characters in it %s)", id)));
    		});
//    		It("should be able to create a lock and unlock a file", () -> {
//				String token = sardine.lock(getUrl(String.format("/document%s", id, id)));
//				sardine.unlock(getUrl(String.format("/document%s", id, id)), token);
//    		});
    		Context("given a root folder", () -> {
    			BeforeEach(() -> {
    				sardine.createDirectory(getUrl(String.format("/root%s", id)));
    			});
        		Context("given a folder can be created", () -> {
        			BeforeEach(() -> {
        				sardine.createDirectory(getUrl(String.format("/root%s/folder%s", id, id)));
        			});
        			It("should be listable", () -> {
            	        assertThat(sardine.exists(getUrl(String.format("/root%s/folder%s", id, id))), is(true));
        			});
        			It("should be renameable", () -> {
        				sardine.move(getUrl(String.format("/root%s/folder%s", id, id)), getUrl(String.format("/root%s/%sfolder", id, id)));
        			});
        			It("should be deletable", () -> {
        				sardine.delete(getUrl(String.format("/root%s/folder%s", id, id)));
            	        assertThat(sardine.exists(getUrl(String.format("/root%s/folder%s", id, id))), is(false));
        			});
        			AfterEach(() -> {
        				try {
        					sardine.delete(getUrl(String.format("/root%s/folder%s", id, id)));
        				} catch (SardineException e) {
        					if (e.getStatusCode() != 404) {
        						throw e;
        					}
        				}
        				try {
        					sardine.delete(getUrl(String.format("/root%s/%sfolder", id, id)));
        				} catch (SardineException e) {
        					if (e.getStatusCode() != 404) {
        						throw e;
        					}
        				}
        			});
        		});
        		Context("given a document can be created", () -> {
        			BeforeEach(() -> {
        				sardine.put(getUrl(String.format("/root%s/document%s", id, id)), "hello world!".getBytes());
        			});
        			It("should be listable", () -> {
            	        assertThat(sardine.exists(getUrl(String.format("/root%s/document%s", id, id))), is(true));
        			});
        			It("should be getable", () -> {
        				InputStream is = sardine.get(getUrl(String.format("/root%s/document%s", id, id)));
            	        assertThat(IOUtils.toString(is), is("hello world!"));
        			});
        			It("should be renameable", () -> {
        				sardine.move(getUrl(String.format("/root%s/document%s", id, id)), getUrl(String.format("/root%s/%sdocument", id, id)));
        			});
        			It("should be deletable", () -> {
        				sardine.delete(getUrl(String.format("/root%s/document%s", id, id)));
            	        assertThat(sardine.exists(getUrl(String.format("/root%s/document%s", id, id))), is(false));
        			});
        			AfterEach(() -> {
        				try {
        					sardine.delete(getUrl(String.format("/root%s/document%s", id, id)));
        				} catch (SardineException e) {
        					if (e.getStatusCode() != 404) {
        						throw e;
        					}
        				}
        				try {
        					sardine.delete(getUrl(String.format("/root%s/%sdocument", id, id)));
        				} catch (SardineException e) {
        					if (e.getStatusCode() != 404) {
        						throw e;
        					}
        				}
        			});
        		});
    		});
    		AfterEach(() -> {
				try {
					sardine.delete(getUrl(String.format("/root%s", id)));
				} catch (SardineException e) {
					if (e.getStatusCode() != 404) {
						throw e;
					}
				}
    		});
    	});
    }
	
	@Test
	public void contextLoads() {
	}
	
	protected String getUrl(String path) throws UnsupportedEncodingException {
		return "http://localhost:" + port + UriUtils.encodePath(path, "UTF-8");
	}
}
