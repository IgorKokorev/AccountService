package account.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FailedSignupResponse {
    Instant timestamp;
    int status;
    String error;
    String message;
    String path;
}
