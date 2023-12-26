package com.team36.config;

import com.team36.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;

@Log4j2
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

    private final DataSource dataSource;

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        log.info("-------------------  filter Chain  ------------------");

        http
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers(new MvcRequestMatcher(introspector,"/")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/loginPro")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/member/loginFail")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/join")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/findPw")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/emailConfirm")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/editor")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/notice/**")).permitAll()
                                .anyRequest().authenticated());

        http
                .formLogin((formLogin) -> formLogin
                        .loginPage("/login")
                        .failureUrl("/member/loginFail")
                        .defaultSuccessUrl("/loginPro", true)
                );

        http
                .csrf((csrf) ->
                        csrf.disable()
                );

        http
                .logout((logout) ->
                        logout.logoutUrl("/logout").logoutSuccessUrl("/")
                );

        http
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                );
        http
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.ignoring().
                requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(new AntPathRequestMatcher( "/css/**"))
                .requestMatchers(new AntPathRequestMatcher( "/js/**"))
                .requestMatchers(new AntPathRequestMatcher( "/img/**"))
                .requestMatchers(new AntPathRequestMatcher( "/vendor/**"))
                .requestMatchers(new AntPathRequestMatcher( "/node_modules/**"));
    }

}