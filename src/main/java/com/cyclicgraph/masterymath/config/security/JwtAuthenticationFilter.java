package com.cyclicgraph.masterymath.config.security;

import com.cyclicgraph.masterymath.user.model.UserEntity;
import com.cyclicgraph.masterymath.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authorizationHeader = request.getHeader("Authorization");

            if (StringUtils.isEmpty(authorizationHeader) || !StringUtils.startsWith(authorizationHeader, "Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authorizationHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);

            if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserEntity userEntity = userService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt)) {
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userEntity, null, userEntity.getAuthorities());

                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(token);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
        } catch (Exception e) {
            log.trace(e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
