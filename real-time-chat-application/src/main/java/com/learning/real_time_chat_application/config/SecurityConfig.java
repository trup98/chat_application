package com.learning.real_time_chat_application.config;

import com.learning.real_time_chat_application.config.jwt.JwtTokenProvider;
import com.learning.real_time_chat_application.dto.response.TokenClaims;
import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.service.UserAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserAuthenticationService userAuthenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @RequestScope
    public TokenClaims claims(HttpServletRequest request, JwtTokenProvider jwtTokenProvider) {
        String token = jwtTokenProvider.resolveToken(request);
        return token != null ? tokenClaims(request) : new TokenClaims();
    }

    public TokenClaims tokenClaims(HttpServletRequest request) {
        return new TokenClaims(
                this.jwtTokenProvider.resolveToken(request),
                this.jwtTokenProvider.getUsername(this.jwtTokenProvider.resolveToken(request)),
                this.jwtTokenProvider.getUserIdFromToken(this.jwtTokenProvider.resolveToken(request)),
                this.jwtTokenProvider.getUserRole(this.jwtTokenProvider.resolveToken(request)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userAuthenticationService.findUserByEmail(username).
                orElseThrow(() -> new UsernameNotFoundException(ExceptionEnum.USER_NOT_FOUND.getValue()));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
