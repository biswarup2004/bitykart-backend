// [file name]: JwtAuthenticationFilter.java
// [file content begin]
package com.bity.bitykart.config;

import com.bity.bitykart.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");
            System.out.println("JWT Filter - Request URI: " + request.getRequestURI());
            System.out.println("JWT Filter - Authorization Header: " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("JWT Filter - No Bearer token found");
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String userEmail = jwtService.getEmailFromToken(token);

            System.out.println("JWT Filter - User Email from token: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userEmail, null, null);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("JWT Filter - Authentication set for user: " + userEmail);
                } else {
                    System.out.println("JWT Filter - Token validation failed");
                }
            }
        } catch (Exception e) {
            // Log the error but don't break the filter chain
            logger.error("JWT authentication error: " + e.getMessage());
            System.out.println("JWT Filter - Error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
// [file content end]