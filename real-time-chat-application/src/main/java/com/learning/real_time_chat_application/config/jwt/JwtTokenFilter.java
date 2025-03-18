package com.learning.real_time_chat_application.config.jwt;

import com.learning.real_time_chat_application.enums.ExceptionEnum;
import com.learning.real_time_chat_application.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        if ((token != null && !token.isEmpty())) {
            Boolean isTokenValid = jwtTokenProvider.validateToken(token);
            if (!isTokenValid) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ExceptionEnum.INVALID_TOKEN.getValue());
                throw new CustomException(ExceptionEnum.INVALID_TOKEN.getMessage(), HttpStatus.UNAUTHORIZED);
            }
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            setHeader(response, jwtTokenProvider.createNewTokenFromToken(token));
        }
        filterChain.doFilter(request, response);
    }

    private void setHeader(HttpServletResponse response, String newToken) {
        response.setHeader("Authorization", newToken);
        response.setHeader("traceId", MDC.get("traceId"));
        response.setHeader("Access-Control-Expose-Headers", "Authorization");
    }
}
