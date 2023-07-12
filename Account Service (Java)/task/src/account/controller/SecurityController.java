package account.controller;

import account.model.SecurityEvent;
import account.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SecurityController {
    private final SecurityEventRepository securityEventRepository;

    @GetMapping("/api/security/events/")
    public ResponseEntity<List<SecurityEvent>> getAllSecurityEvents() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(securityEventRepository.findAll());
    }
}
