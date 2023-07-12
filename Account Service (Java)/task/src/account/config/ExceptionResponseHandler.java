package account.config;

import account.DTO.FailedSignupResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@ControllerAdvice
public class ExceptionResponseHandler {

/*    @ExceptionHandler(value = ResponseStatusException.class)
    ResponseEntity<FailedSignupResponse> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request
    ) {
        Instant timestamp = Instant.now();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(new FailedSignupResponse(timestamp, ex.getStatusCode().value(), "Bad Request", ex.getMessage(), path));
    }*/

    @ExceptionHandler(value = MyException.class)
    ResponseEntity<FailedSignupResponse> handleResponseStatusException(
            MyException ex, WebRequest request
    ) {
        Instant timestamp = Instant.now();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String error;
        switch (ex.getStatus()) {
            case BAD_REQUEST -> error = "Bad Request";
            case NOT_FOUND -> error = "Not Found";
            case FORBIDDEN -> error = "Forbidden";
            default -> error = "Bad Request";

        }
        return ResponseEntity
                .status(ex.getStatus())
                .body(new FailedSignupResponse(timestamp, ex.getStatus().value(), error, ex.getMessage(), path));
    }

    /**
     * handlerOtherExceptions handles any unhandled exceptions.
     */
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<Object> handleOtherExceptions(Exception ex, WebRequest request) {
        String requestUri = ((ServletWebRequest) request).getRequest().getRequestURI();
        ExceptionMessage exceptionMessage = new ExceptionMessage(ex.getMessage(), requestUri);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlAllowOrigin("*");
        return new ResponseEntity<Object>(exceptionMessage, headers, HttpStatus.BAD_REQUEST);
    }
}
