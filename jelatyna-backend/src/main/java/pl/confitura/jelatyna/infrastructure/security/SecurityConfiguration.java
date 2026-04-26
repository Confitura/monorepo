package pl.confitura.jelatyna.infrastructure.security;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    static final String ADMIN = "ROLE_ADMIN";

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(it -> it
                        .sessionCreationPolicy(STATELESS)
                )
                .authorizeHttpRequests(it -> it
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/likes/*").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/**").authenticated()
                        .requestMatchers("/actuator/info").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator/**").hasAnyAuthority(ADMIN)
                        .requestMatchers("/**").permitAll())

                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

}
