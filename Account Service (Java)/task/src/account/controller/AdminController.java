package account.controller;

import account.DTO.*;
import account.config.MyException;
import account.model.Role;
import account.model.User;
import account.repository.RoleRepository;
import account.repository.UserRepository;
import account.service.RoleCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleCache roleCache;

    @GetMapping("/api/admin/user/")
    public ResponseEntity<List<SignupResponse>> getAllUsers() {

        List<User> allUsers = userRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(
                allUsers.stream()
                        .sorted()
                        .map(user -> new SignupResponse(user))
                        .toList());

    }

    @DeleteMapping("/api/admin/user/{email}")
    public ResponseEntity<UserStatusResponse> getAllUsers(@PathVariable String email) throws MyException {

        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() ->
                new MyException(HttpStatus.NOT_FOUND, "User not found!"));

        if (user.getRoles().contains(roleCache.getAdmin()))
            throw new MyException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");

        userRepository.delete(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserStatusResponse(email, "Deleted successfully!"));
    }

    @PutMapping("api/admin/user/access")
    public ResponseEntity<StatusResponse> manageUserAccess(@RequestBody UserOperationRequest request) throws MyException {

    }

    @PutMapping("api/admin/user/role")
    public ResponseEntity<SignupResponse> changeRole(@RequestBody ChangeRoleRequest request) throws MyException {

        User user = userRepository.findByEmailIgnoreCase(request.getUser()).orElseThrow(() ->
                new MyException(HttpStatus.NOT_FOUND, "User not found!"));

        Role role = roleRepository.findByRole(request.getRole()).orElseThrow(() ->
                new MyException(HttpStatus.NOT_FOUND, "Role not found!"));

        switch (request.getOperation()) {
            case GRANT -> grantRole(user, role);
            case REMOVE -> removeRole(user, role);
        }

        user = userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SignupResponse(user));
    }

    private void grantRole(User user, Role role) throws MyException {

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
    }

    private void removeRole(User user, Role role) throws MyException {

        if (!user.getRoles().contains(role))
            throw new MyException(HttpStatus.BAD_REQUEST, "The user does not have a role!");

        if (role.equals(roleCache.getAdmin()))
            throw new MyException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");

        if (user.getRoles().size() == 1)
            throw new MyException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");

        user.getRoles().remove(role);

    }
}
