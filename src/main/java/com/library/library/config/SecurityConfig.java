package com.library.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/", "/web/**", "/css/**", "/js/**", "/WEB-INF/**") // Вернул WEB-INF потому что SP рендерится через forward (ContentNegotiatingViewResolver → Forwarding to [/WEB-INF/views/login.jsp]), а forward повторно проходит цепочку Spring Security фильтров. Без матчера запрос попадал в apiFilterChain (Order 2) и форвард на JSP получал 401 вместо 200.
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/web/login", "/web/register", "/css/**", "/js/**", "/WEB-INF/**").permitAll()
                        .requestMatchers("/web/users/**", "/web/books/new", "/web/authors/new").hasRole("EDITOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/web/login")
                        .loginProcessingUrl("/web/login")
                        .defaultSuccessUrl("/web/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/web/logout")
                        .logoutSuccessUrl("/web/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books/**", "/api/authors/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
