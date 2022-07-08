package gettingstarted;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringContentApplication.class, args);
    }

    @Bean
    public StoredRenditionsEventHandler storedRenditionsEventHandler() {
        return new StoredRenditionsEventHandler();
    }
}

