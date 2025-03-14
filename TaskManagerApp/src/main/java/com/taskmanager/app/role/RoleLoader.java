package com.taskmanager.app.role;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RoleLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;

    public RoleLoader(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(final ApplicationArguments args) {
        if (roleRepository.count() != 0) {
            return;
        }
        log.info("initializing roles");
        final Role roleSuperAdminRole = new Role();
        roleSuperAdminRole.setName("ROLE_SUPER_ADMIN");
        roleRepository.save(roleSuperAdminRole);
        final Role roleAdminRole = new Role();
        roleAdminRole.setName("ROLE_ADMIN");
        roleRepository.save(roleAdminRole);
    }

}
