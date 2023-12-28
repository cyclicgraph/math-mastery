package com.cyclicgraph.masterymath.config;

import com.cyclicgraph.masterymath.permission.PermissionRepository;
import com.cyclicgraph.masterymath.role.RoleRepository;
import com.cyclicgraph.masterymath.user.model.Permission;
import com.cyclicgraph.masterymath.user.model.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements ApplicationListener<ApplicationStartedEvent> {
    private final RoleRepository roleRepository;
    private final PermissionRepository privilegeRepository;
    private boolean alreadySetup = false;

    @Override
    @Transactional
    // temporary - remove after integration with liquibase
    public void onApplicationEvent(@NotNull ApplicationStartedEvent event) {
        if (alreadySetup) return;

        Permission readPermission = createPermissionIfNotFound("READ_PRIVILEGE");
        Permission writePermission = createPermissionIfNotFound("WRITE_PRIVILEGE");
        createRoleIfNotFound("ROLE_ADMIN", Arrays.asList(readPermission, writePermission));
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPermission));

        alreadySetup = true;
    }

    @Transactional
    private Permission createPermissionIfNotFound(String name) {
        return privilegeRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(name);
                    return privilegeRepository.save(permission);
                });
    }

    @Transactional
    private Role createRoleIfNotFound(String name, Collection<Permission> permissions) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setPermissions(permissions);
            return roleRepository.save(role);
        });
    }
}
