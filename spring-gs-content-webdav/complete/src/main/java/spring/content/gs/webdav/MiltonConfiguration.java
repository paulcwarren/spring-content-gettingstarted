package spring.content.gs.webdav;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.emory.mathcs.backport.java.util.Collections;
import io.milton.config.HttpManagerBuilder;
import io.milton.ent.config.HttpManagerBuilderEnt;
import io.milton.http.ResourceFactory;
import io.milton.http.annotated.AnnotationResourceFactory;
import io.milton.http.http11.DefaultHttp11ResponseHandler;
import io.milton.http.http11.auth.BasicAuthHandler;
import spring.content.gs.webdav.resources.FileContentRepository;
import spring.content.gs.webdav.resources.FileRepository;
import spring.content.gs.webdav.resources.FolderRepository;

/**
 *
 */
@Configuration
@EnableConfigurationProperties(MiltonProperties.class)
public class MiltonConfiguration {

    @Autowired
    MiltonProperties miltonProperties;
    
    @Autowired 
    private FolderRepository folders;

    @Autowired
    private FileRepository files;

    @Autowired 
    private FileContentRepository contents;

	@Bean
	public File fileSystemRoot() throws IOException {
		return Files.createTempDirectory("").toFile();
	}
    
    @Bean
    FilterRegistrationBean filterRegistrationBean()
    {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(springMiltonFilterBean());
        bean.addUrlPatterns("/*");
        return bean;
    }

    @Bean
    SpringMiltonFilterBean springMiltonFilterBean()
    {
        return new SpringMiltonFilterBean();
    }

    @Bean
    ResourceFactory resourceFactory()
    {
    	AnnotationResourceFactory resourceFactory = new AnnotationResourceFactory();
    	return resourceFactory;
    }
    
    @Bean
    HttpManagerBuilder httpManagerBuilder()
    {
        HttpManagerBuilderEnt builder = new HttpManagerBuilderEnt();
        builder.setResourceFactory(resourceFactory());
        builder.setControllerPackagesToScan("spring.content.gs.webdav.resources");
        builder.setBuffering(DefaultHttp11ResponseHandler.BUFFERING.never);
        builder.setEnableCompression(false);
        //builder.setAuthenticationHandlers(Collections.singletonList(new BasicAuthHandler()));
        builder.getRootContext().put(folders);
        builder.getRootContext().put(files);
        builder.getRootContext().put(contents);
        return builder;
    }
}
