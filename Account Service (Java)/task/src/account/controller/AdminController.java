package account.controller;

import account.DTO.*;
import account.config.MyException;
import account.model.Action;
import account.model.Role;
import account.model.User;
import account.repository.UserRepository;
import account.service.RoleCache;
import account.service.SecurityService;
import account.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AdminController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleCache roleCache;
    private final SecurityService securityService;

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
    public ResponseEntity<UserStatusResponse> getAllUsers(@PathVariable String email, @AuthenticationPrincipal UserDetails principle) throws MyException {

        User user = userService.getUser(email);

        if (user.getRoles().contains(roleCache.getAdmin()))
            throw new MyException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");

        securityService.createSecurityEvent(
                LocalDateTime.now(),
                Action.DELETE_USER,
                principle.getUsername(),
                user.getEmail(),
                "/api/admin/user"
        );

        userRepository.delete(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserStatusResponse(email, "Deleted successfully!"));
    }

    @PutMapping("/api/admin/user/access")
    public ResponseEntity<StatusResponse> manageUserAccess(
            @RequestBody UserOperationRequest request,
            @AuthenticationPrincipal UserDetails principal) throws MyException {
        User user = userService.getUser(request.getUser());

        if (user.getRoles().contains(roleCache.getAdmin()))
            throw new MyException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");

        Action action;
        String prefix;
        if (request.getOperation().equals(UserOperation.LOCK)) {
            user.setNonLocked(false);
            action = Action.LOCK_USER;
            prefix = "L";
        }
        else {
            user.setNonLocked(true);
            user.setFailedLogins(0);
            action = Action.UNLOCK_USER;
            prefix = "Unl";
        }

        user = userService.save(user);

        securityService.createSecurityEvent(
                LocalDateTime.now(),
                action,
                principal.getUsername(),
                prefix + "ock user " + user.getEmail(),
                "/api/admin/user/access"
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new StatusResponse("User " + user.getEmail() + " " + request.getOperation().toString().toLowerCase() + "ed!"));
    }

    @PutMapping("/api/admin/user/role")
    public ResponseEntity<SignupResponse> changeRole(
            @RequestBody ChangeRoleRequest request,
            @AuthenticationPrincipal UserDetails principal) throws MyException {

        User user = userService.getUser(request.getUser());
        Role role = userService.getRole(request.getRole());

        switch (request.getOperation()) {
            case GRANT -> userService.grantRole(user, role, principal);
            case REMOVE -> userService.removeRole(user, role, principal);
        }

        user = userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new SignupResponse(user));
    }


}
