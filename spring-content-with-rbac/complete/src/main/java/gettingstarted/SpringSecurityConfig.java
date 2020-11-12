package gettingstarted;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public InMemoryUserDetailsManager userDetailsManager() {

		UserBuilder builder = User.withDefaultPasswordEncoder();

		UserDetails eric = builder.username("eric").password("wimp").roles("READER").build();
		UserDetails paul = builder.username("paul").password("warren").roles("READER", "AUTHOR").build();

		return new InMemoryUserDetailsManager(eric, paul);
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/files/**/content").hasRole("READER")
                .antMatchers(HttpMethod.PUT, "/files/**/content").hasRole("AUTHOR")
                .anyRequest().permitAll()
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }
}