package gettingstarted;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@SpringBootApplication
public class SpringContentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringContentApplication.class, args);
    }

    @Configuration
    @EnableJpaRepositories(basePackages = {"gettingstarted", "org.springframework.versions"})
    public static class StoreConfig {
    }

    @Configuration
    @EnableWebSecurity
    public static class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

        protected static String REALM = "SPRING_CONTENT";

        @Bean
        public AuthenticationEntryPoint getBasicAuthEntryPoint() {
            return new AuthenticationEntryPoint();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
        }
    }

    public static class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

        @Override
        public void commence(final HttpServletRequest request, final HttpServletResponse response,
                             final AuthenticationException authException) throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");

            PrintWriter writer = response.getWriter();
            writer.println("HTTP Status 401 : " + authException.getMessage());
        }

        @Override
        public void afterPropertiesSet() {
            setRealmName(SpringContentApplication.SpringSecurityConfig.REALM);
            super.afterPropertiesSet();
        }
    }
}

