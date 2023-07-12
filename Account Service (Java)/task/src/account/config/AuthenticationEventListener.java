package account.config;

import account.model.User;
import account.repository.SecurityEventRepository;
import account.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {
    private final UserService userService;


/*    @EventListener
    public void authenticationFailed(AuthenticationFailureBadCredentialsEvent event) {

        String username = (String) event.getAuthentication().getPrincipal();



        log.info("Faled login with username: " + username + " at " + event.getException());

        Optional<User> optUser = userService.getOptionalUser(username);

        if (optUser.isEmpty()) username = "Anonymous";
        else {
            User user = optUser.get();
            user.incFailedLogins();
            user = userService.save(user);
        }

        // update the failed login count for the user
        // ...
    }*/

    @EventListener
    public void authenticationSuccess(AuthenticationSuccessEvent event) {

        User user = (User) event.getAuthentication().getPrincipal();
        log.info(((UsernamePasswordAuthenticationToken) event.getSource()).getDetails().toString());

        log.info("Successful login with username: " + user.getUsername() + ", failed login attempts: " + user.getFailedLogins());
        user.setFailedLogins(0);
        user = userService.save(user);
        log.info("Now failed login attempts: " + user.getFailedLogins());
    }

}
