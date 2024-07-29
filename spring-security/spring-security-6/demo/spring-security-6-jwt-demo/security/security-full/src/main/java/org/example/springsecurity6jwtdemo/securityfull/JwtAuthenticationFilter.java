package org.example.springsecurity6jwtdemo.securityfull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.springsecurity6jwtdemo.securitycore.DefaultJwtHttpClient;
import org.example.springsecurity6jwtdemo.securitycore.JwtHelper;
import org.example.springsecurity6jwtdemo.securitycore.User;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtHelper jwtHelper;
    private final DefaultJwtHttpClient defaultJwtHttpClient;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtHelper.extractUserName(jwt);
        if (StringUtils.isNotEmpty(userEmail)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = defaultJwtHttpClient.loaderUser(jwt);
            if (jwtHelper.isTokenValid(jwt) && user != null) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                ContextUser contextUser = ContextUser.builder().id(user.getId()).role(user.getRole()).email(user.getEmail()).firstName(user.getFirstName()).lastName(user.getLastName()).build();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        contextUser, null, contextUser.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
