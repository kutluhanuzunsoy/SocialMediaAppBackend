package com.project.socialmediaapp.security;

import com.project.socialmediaapp.services.UserDetailsServiceImp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTTokenProvider JWTTokenProvider;
    private final UserDetailsServiceImp userDetailsService;

    public JWTAuthenticationFilter(JWTTokenProvider JWTTokenProvider, UserDetailsServiceImp userDetailsService) {
        this.JWTTokenProvider = JWTTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwtToken = extractJWTfromRequest(request);
            if(StringUtils.hasText(jwtToken) && JWTTokenProvider.validateToken(jwtToken)) {
                Long id = JWTTokenProvider.getUserIdFromJWT(jwtToken);
                UserDetails user = userDetailsService.loadUserById(id);
                if(user != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch(Exception e) {
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractJWTfromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
