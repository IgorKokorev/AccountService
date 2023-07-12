package account.controller;

import account.DTO.ChangePasswordRequest;
import account.DTO.ChangePasswordResponse;
import account.DTO.SignupRequest;
import account.DTO.SignupResponse;
import account.config.MyException;
import account.model.Action;
import account.model.User;
import account.repository.UserRepository;
import account.service.RoleCache;
import account.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final List<String> breachedPasswords = Arrays.asList("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final RoleCache roleCache;
    private final SecurityService securityService;

    @PostMapping("/api/auth/signup")
    public ResponseEntity<SignupResponse> signup(
            @RequestBody @Valid SignupRequest request) throws MyException {

        if(!isCorrectPassword(request.getPassword()))
            throw new MyException(HttpStatus.BAD_REQUEST, "The password length must be at least 12 chars!");
        if(isPasswordBreached(request.getPassword()))
            throw new MyException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");

        if(!isCorrectEmail(request.getEmail()))
            throw new MyException(HttpStatus.BAD_REQUEST, "Incorrect email");
        if (userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent())
            throw new MyException(HttpStatus.BAD_REQUEST, "User exist!");

        request.setEmail(request.getEmail().toLowerCase());
        request.setPassword(encoder.encode(request.getPassword()));
        User user = new User(request);

        if (userRepository.findAll().isEmpty()) user.getRoles().add(roleCache.getAdmin());
        else user.getRoles().add(roleCache.getUser());

        user = userRepository.save(user);

        securityService.createSecurityEvent(LocalDateTime.now(), Action.CREATE_USER, "Anonymous", request.getEmail(), "/api/auth/signup");

        return ResponseEntity.status(HttpStatus.OK).body(new SignupResponse(user));

    }

    private boolean isPasswordBreached(String password) {
        return breachedPasswords.contains(password);
    }

    private boolean isCorrectPassword(String password) {
        return password.length() >= 12;
    }

    private boolean isCorrectEmail(String email) {
        String emailRegexp = "[\\w.-]+@acme\\.com";
        return email.matches(emailRegexp);
    }

    @PostMapping("/api/auth/changepass")
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails principal) throws MyException {

        if(!isCorrectPassword(request.getNew_password()))
            throw new MyException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!");
        if(isPasswordBreached(request.getNew_password()))
            throw new MyException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");

        User user = (User) principal;

        if (encoder.matches(request.getNew_password(), user.getPassword()))
            throw new MyException(HttpStatus.BAD_REQUEST, "The passwords must be different!");

        user.setPassword(encoder.encode(request.getNew_password()));
        user = userRepository.save(user);

        securityService.createSecurityEvent(
                LocalDateTime.now(),
                Action.CHANGE_PASSWORD,
                user.getEmail(),
                user.getEmail(),
                "/api/auth/changepass"
        );

        return ResponseEntity.status(HttpStatus.OK).body(new ChangePasswordResponse(user.getEmail()));
    }

}
