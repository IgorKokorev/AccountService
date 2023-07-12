package account.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class MyException extends Exception {
    private HttpStatus status;
    private String message;
}
