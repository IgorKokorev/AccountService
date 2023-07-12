package account.service;

import account.model.Action;
import account.model.SecurityEvent;
import account.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final SecurityEventRepository securityEventRepository;

    public void createSecurityEvent(LocalDateTime date, Action action, String subject, String object, String path) {
        SecurityEvent event = new SecurityEvent(date, action, subject, object, path);
        event = securityEventRepository.save(event);
    }
}
