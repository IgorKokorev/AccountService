package account.controller;

import account.DTO.GetSalaryResponse;
import account.config.MyException;
import account.model.Salary;
import account.model.User;
import account.repository.SalaryRepository;
import account.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final UserRepository userRepository;
    private final SalaryRepository salaryRepository;
    private final String periodRegexp = "((0[1-9])|(1[0-2]))-[0-9]{4}";

    @GetMapping("/api/empl/payment")
    public ResponseEntity<Object> getOnePayment(
            @RequestParam(required = false) String period,
            @AuthenticationPrincipal UserDetails user) throws MyException {

        if (period == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(salaryRepository.findByUserOrderByYearDescMonthDesc((User) user).stream()
                            .map(salary -> new GetSalaryResponse(salary))
                            .toList()
                    );
        }

        if (!period.matches(periodRegexp))
            throw new MyException(HttpStatus.BAD_REQUEST, "Period '" + period + "' incorrect, ");

        Optional<Salary> optSalary = salaryRepository.findByUserAndPeriod((User) user, period);

        if (optSalary.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GetSalaryResponse(optSalary.get()));
        }
    }

}
