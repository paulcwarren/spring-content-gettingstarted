package gettingstarted;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

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
    public static class SpringSecurityConfig /*extends WebSecurityConfigurerAdapter*/ {

        protected static String REALM = "SPRING_CONTENT";

        @Autowired
        public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
            // Enable if spring-doc apps supports user accounts in the future
            auth.inMemoryAuthentication().
                    withUser(User.withDefaultPasswordEncoder().username("paul").password("warren").roles("ADMIN").
//                    withUser(User.withDefaultPasswordEncoder().username("john123").password("password").roles("USER").
                        build());

        }

        @Bean
        public AuthenticationEntryPoint getBasicAuthEntryPoint() {
            return new AuthenticationEntryPoint();
        }

//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//
//            http.csrf().disable()
//                    .authorizeRequests()
//                    .antMatchers("/admin/**").hasRole("ADMIN")
//                    .and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
//                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
//        }
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            return http.build();
        }
//        @Override
//        public void configure(WebSecurity web) throws Exception {
//            web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
//        }
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers(HttpMethod.OPTIONS, "/**");
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

