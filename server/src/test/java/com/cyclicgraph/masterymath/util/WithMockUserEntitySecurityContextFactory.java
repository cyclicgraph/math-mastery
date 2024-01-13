package com.cyclicgraph.masterymath.util;

import com.cyclicgraph.masterymath.user.model.Role;
import com.cyclicgraph.masterymath.user.model.UserEntity;
import com.cyclicgraph.masterymath.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class WithMockUserEntitySecurityContextFactory implements WithSecurityContextFactory<WithMockUserEntity> {
    private final UserRepository userRepository;

    @Override
    public SecurityContext createSecurityContext(WithMockUserEntity annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserEntity user = new UserEntity(UUID.randomUUID(), annotation.username(), annotation.password(), "email@email.com", Arrays.stream(annotation.roles()).map(Role::valueOf).toList());
        userRepository.save(user);

        Authentication auth =
                UsernamePasswordAuthenticationToken.authenticated(user, "password", user.getAuthorities());
        context.setAuthentication(auth);

        return context;
    }
}
