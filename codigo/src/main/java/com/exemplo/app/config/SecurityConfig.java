package com.exemplo.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.exemplo.app.repository.UsuarioRepository;
import com.exemplo.app.security.JwtAuthenticationFilter;
import com.exemplo.app.security.TokenService;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenService tokenService,
            UsuarioRepository usuarioRepository) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // Logout tratado pela própria aplicação (limpa o cookie do JWT)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // Páginas públicas, estáticos e logout
                        .requestMatchers("/", "/login", "/logout", "/error", "/css/**", "/js/**", "/images/**",
                                "/favicon.ico").permitAll()
                        // Áreas por papel (usuário resolvido do JWT no cookie/header)
                        .requestMatchers("/secretaria/**").hasRole("SECRETARIO")
                        .requestMatchers("/professor/**").hasRole("PROFESSOR")
                        .requestMatchers("/aluno/**").hasRole("ALUNO")
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        // API responde 401 JSON; páginas redirecionam para o login
                        .authenticationEntryPoint((request, response, ex) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(401);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"erro\":\"Não autenticado\"}");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                        .accessDeniedHandler((request, response, ex) -> response.sendRedirect("/login")))
                .addFilterBefore(new JwtAuthenticationFilter(tokenService, usuarioRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}