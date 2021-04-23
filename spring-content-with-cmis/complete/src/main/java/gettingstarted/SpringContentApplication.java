package gettingstarted;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.content.cmis.EnableCmis;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.versions.jpa.config.JpaLockingAndVersioningConfig;

@SpringBootApplication
@EnableCmis(basePackages = "gettingstarted",
        id = "1",
        name = "spring-content-with-cmis",
        description = "Spring Content CMIS Getting Started Guide",
        vendorName = "Spring Content OSS",
        productName = "Spring Content CMIS Connector",
        productVersion = "1.0.0")
@Import(JpaLockingAndVersioningConfig.class)
@EnableJpaRepositories(
        basePackages={  "gettingstarted",
                        "org.springframework.versions"},
        considerNestedRepositories=true)
@EnableFilesystemStores
public class SpringContentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringContentApplication.class, args);
	}

	@Configuration
	@EnableWebSecurity
	@EnableJpaAuditing
	public static class SecurityConfig extends WebSecurityConfigurerAdapter {

	    @Autowired
	    public void configureGlobal(AuthenticationManagerBuilder auth)
	            throws Exception {
	        auth.
	                inMemoryAuthentication()
	                    .withUser("test")
	                    .password("{noop}test")
	                        .roles("USER");
	    }

	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        http
	                .csrf()
	                    .disable()
	                .authorizeRequests()
	                    .anyRequest()
	                        .authenticated()
	                    .and()
	                        .httpBasic();
	    }

	    @Bean
	    public AuditorAware<String> objectAuditor() {
	        return new AuditorAware<String>() {
	            @Override
	            public Optional<String> getCurrentAuditor() {
	                return Optional.ofNullable(SecurityContextHolder.getContext())
	                        .map(SecurityContext::getAuthentication)
	                        .filter(Authentication::isAuthenticated)
	                        .map(Authentication::getPrincipal)
	                        .map((u) -> ((User)u).getUsername());
	            }
	        };
	    }
	}
}
