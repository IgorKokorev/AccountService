package account.service;

import account.config.MyException;
import account.model.Action;
import account.model.Role;
import account.model.User;
import account.repository.RoleRepository;
import account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleCache roleCache;
    private final RoleRepository roleRepository;
    private final SecurityService securityService;

    public User getUser(String email) throws MyException {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new MyException(HttpStatus.NOT_FOUND, "Employee " + email + " not found"));
    }


    public Optional<User> getOptionalUser(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void grantRole(User user, Role role, UserDetails principal) throws MyException {

        if (user.getRoles().contains(role))
            throw new MyException(HttpStatus.BAD_REQUEST, "The user already has this role!");

        if (role.equals(roleCache.getAdmin()) &&
                (
                        user.getRoles().contains(roleCache.getUser()) ||
                                user.getRoles().contains(roleCache.getAcct()) ||
                                user.getRoles().contains(roleCache.getAuditor())
                )
        ) {
            throw new MyException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }

        if (user.getRoles().contains(roleCache.getAdmin()) &&
                (
                        role.equals(roleCache.getUser()) ||
                                role.equals(roleCache.getAcct()) ||
                                role.equals(roleCache.getAuditor())
                )
        ) {
            throw new MyException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        }

        user.getRoles().add(role);

        securityService.createSecurityEvent(
                LocalDateTime.now(),
                Action.GRANT_ROLE,
                principal.getUsername(),
                "Grant role " + role.getRole() + " to " + user.getEmail(),
                "/api/admin/user/role"
        );
    }

    public void removeRole(User user, Role role, UserDetails principal) throws MyException {

        if (!user.getRoles().contains(role))
            throw new MyException(HttpStatus.BAD_REQUEST, "The user does not have a role!");

        if (role.equals(roleCache.getAdmin()))
            throw new MyException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");

        if (user.getRoles().size() == 1)
            throw new MyException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");

        user.getRoles().remove(role);

        securityService.createSecurityEvent(
                LocalDateTime.now(),
                Action.REMOVE_ROLE,
                principal.getUsername(),
                "Remove role " + role.getRole() + " from " + user.getEmail(),
                "/api/admin/user/role"
        );

    }


    public Role getRole(String roleName) throws MyException {
        return roleRepository.findByRole(roleName).orElseThrow(() ->
                new MyException(HttpStatus.NOT_FOUND, "Role not found!"));
    }
}
