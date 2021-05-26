package gettingstarted;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public RestHighLevelClient client() {
        return ElasticsearchTestContainer.client();
    }
}
