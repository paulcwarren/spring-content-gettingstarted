package gettingstarted;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.s3.config.AbstractS3ContentRepositoryConfiguration;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

@SpringBootApplication
public class SpringContentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringContentApplication.class, args);
	}
	
	@Configuration
	public static class S3Configuration extends AbstractS3ContentRepositoryConfiguration {
		
		@Override
		public String bucket() {
			return "spring-eg-content-s3";
		}

		@Override
		public Region region() {
			return Region.getRegion(Regions.US_WEST_1);
		}

	}
}
