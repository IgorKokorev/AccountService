package account.config;

import account.model.Action;
import account.model.User;
import account.service.RoleCache;
import account.service.SecurityService;
import account.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Optional;

import org.json.*;

// Handles auth error
@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final SecurityService securityService;
    private final UserService userService;
    private final RoleCache roleCache;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        LocalDateTime timestamp = LocalDateTime.now();
        String path = request.getRequestURI();

        String email = "Anonymous";
        String message = "Unauthorized";
        boolean isNonLocked = true;

        // get user from request and save new failed login nr
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":");

            Optional<User> optUser = Optional.empty();
            if (values.length > 0) {
                email = values[0];
                optUser = userService.getOptionalUser(email);

                if (optUser.isPresent()) {
                    User user = optUser.get();

                    if (user.isAccountNonLocked()) {
                        securityService.createSecurityEvent(timestamp, Action.LOGIN_FAILED, email, path, path);

                        user.incFailedLogins();
                        if (user.getRoles().contains(roleCache.getAdmin())) {
                            user.setNonLocked(true);
                        }
                        user = userService.save(user);
                        isNonLocked = user.isNonLocked();

                        if (!isNonLocked) {
                            securityService.createSecurityEvent(timestamp, Action.BRUTE_FORCE, email, path, path);
                            securityService.createSecurityEvent(timestamp, Action.LOCK_USER, email, "Lock user " + email, path);
                            message = "User account is locked";
                        }
                    } else {
                        message = "User account is locked";
                    }
                } else {
                    securityService.createSecurityEvent(timestamp, Action.LOGIN_FAILED, email, path, path);
                }
            }
        }


        response.getWriter().write(new JSONObject()
                .put("timestamp", timestamp)
                .put("status", 401)
                .put("error", "Unauthorized")
                .put("message", message)
                .put("path", path)
                .toString());
    }
}