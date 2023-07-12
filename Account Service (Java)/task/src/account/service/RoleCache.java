package account.service;

import account.model.Role;
import account.repository.RoleRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Setter
@Component
public class RoleCache {
    private Role user;
    private Role admin;
    private Role acct;
    private Role auditor;


    private RoleRepository roleRepository;

    @Autowired
    public RoleCache(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            this.admin = roleRepository.save(new Role("ADMINISTRATOR"));
            this.user = roleRepository.save(new Role("USER"));
            this.acct = roleRepository.save(new Role("ACCOUNTANT"));
            this.auditor = roleRepository.save(new Role("AUDITOR"));

        } catch (Exception e) {
            log.warn("Error creating roles");
        }
    }
}