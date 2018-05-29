package pl.confitura.jelatyna.infrastructure.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    static final String ADMIN = "ROLE_ADMIN";

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()

                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.DELETE,"/likes/*").permitAll()
                    .antMatchers(HttpMethod.DELETE,"/**").authenticated()
                    .antMatchers("/api/actuator/*").hasAnyAuthority(ADMIN)
                    .antMatchers("/**/*").permitAll()

                .and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

}
