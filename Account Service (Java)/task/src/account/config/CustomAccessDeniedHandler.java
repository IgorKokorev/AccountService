package account.config;

import account.model.Action;
import account.model.User;
import account.service.SecurityService;
import account.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final SecurityService securityService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        LocalDateTime timestamp = LocalDateTime.now();
        String path = request.getRequestURI();

        // get user from request and save new failed login nr
        final String authorization = request.getHeader("Authorization");
        String base64Credentials = authorization.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        // credentials = username:password
        final String[] values = credentials.split(":");
        String username = values[0];

        securityService.createSecurityEvent(
                timestamp,
                Action.ACCESS_DENIED,
                username,
                path,
                path
        );

        response.getWriter().write(new JSONObject()
                .put("timestamp", timestamp)
                .put("status", 403)
                .put("error", "Forbidden")
                .put("message", "Access Denied!")
                .put("path", path)
                .toString());
    }

}
