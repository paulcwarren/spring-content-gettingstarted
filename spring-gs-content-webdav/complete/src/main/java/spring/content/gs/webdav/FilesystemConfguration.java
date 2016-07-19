package spring.content.gs.webdav;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.emc.spring.content.fs.config.EnableFilesystemContentRepositories;

@Configuration
@EnableFilesystemContentRepositories
public class FilesystemConfguration {

	@Bean
	public File fileSystemRoot() throws IOException {
		return Files.createTempDirectory("spring-webdav").toFile();
	}

}
