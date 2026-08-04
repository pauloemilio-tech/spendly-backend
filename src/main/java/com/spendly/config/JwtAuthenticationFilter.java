package com.spendly.config;

import com.spendly.domain.service.CustomUserDetailsService;
import com.spendly.domain.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final Set<String> PUBLIC_POST_ENDPOINTS = Set.of("/auth/login", "/auth/guest", "/customers");

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        return HttpMethod.POST.matches(request.getMethod())
                && PUBLIC_POST_ENDPOINTS.contains(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn("Header Authorization invalido para {} {}", request.getMethod(), request.getRequestURI());
            commenceUnauthorized(
                    request,
                    response,
                    new BadCredentialsException("Header Authorization deve usar o formato Bearer <token>")
            );
            return;
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            log.warn("JWT vazio enviado para {} {}", request.getMethod(), request.getRequestURI());
            commenceUnauthorized(request, response, new BadCredentialsException("Token JWT nao informado"));
            return;
        }

        if (!jwtService.isTokenValid(token)) {
            log.warn("JWT invalido/expirado para {} {}", request.getMethod(), request.getRequestURI());
            commenceUnauthorized(request, response, new BadCredentialsException("Token JWT invalido ou expirado"));
            return;
        }

        String cpf = jwtService.extractCpf(token);

        if (cpf == null || cpf.isBlank()) {
            log.warn("JWT sem subject valido para {} {}", request.getMethod(), request.getRequestURI());
            commenceUnauthorized(request, response, new BadCredentialsException("Token JWT sem subject valido"));
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(cpf);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (UsernameNotFoundException ex) {
                log.warn("Usuario do JWT nao encontrado (cpf={}) em {} {}", cpf, request.getMethod(), request.getRequestURI());
                commenceUnauthorized(
                        request,
                        response,
                        new InsufficientAuthenticationException("Token JWT com usuario inexistente", ex)
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void commenceUnauthorized(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException exception
    ) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        authenticationEntryPoint.commence(request, response, exception);
    }
}
