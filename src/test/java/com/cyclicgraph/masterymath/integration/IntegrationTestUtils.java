package com.cyclicgraph.masterymath.integration;

import com.cyclicgraph.masterymath.auth.model.RefreshToken;
import com.cyclicgraph.masterymath.auth.repository.RefreshTokenRepository;
import com.cyclicgraph.masterymath.role.RoleRepository;
import com.cyclicgraph.masterymath.user.model.UserEntity;
import com.cyclicgraph.masterymath.user.repository.UserRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Component
public class IntegrationTestUtils {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    public Pair<UserEntity, RefreshToken> createUserWithRefreshToken() {
        UserEntity user = new UserEntity(UUID.randomUUID(), "user", passwordEncoder.encode("password"), "user@user.com", Collections.singletonList(roleRepository.findByName("ROLE_USER").get()));
        RefreshToken refreshToken = new RefreshToken(null, System.currentTimeMillis() + (1000000 * 1000), UUID.randomUUID(), user);

        return Pair.of(userRepository.save(user), refreshTokenRepository.save(refreshToken));
    }
}
