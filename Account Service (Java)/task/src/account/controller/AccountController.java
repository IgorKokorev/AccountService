package account.controller;

import account.DTO.PostSalaryRequest;
import account.DTO.StatusResponse;
import account.config.MyException;
import account.model.Salary;
import account.model.User;
import account.repository.SalaryRepository;
import account.repository.UserRepository;
import account.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;
    private final SalaryRepository salaryRepository;

    private final String periodRegexp = "((0[1-9])|(1[0-2]))-[0-9]{4}";

    @PostMapping("/api/acct/payments")
    public ResponseEntity<StatusResponse> salary(
            @RequestBody List<@Valid PostSalaryRequest> listOfSalaries) throws MyException {

        String error = checkListOfSalaries(listOfSalaries);
        if (!error.isEmpty())
            throw new MyException(HttpStatus.BAD_REQUEST, error);

        salaryRepository.saveAll(
                listOfSalaries.stream()
                        .map(request -> new Salary(
                                userService.getOptionalUser(request.getEmployee()).orElseThrow(),
                                request.getPeriod(),
                                request.getSalary()))
                        .toList()
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(new StatusResponse("Added successfully!"));
    }



    @PutMapping("/api/acct/payments")
    public ResponseEntity<StatusResponse> updateSalary(
            @RequestBody @Valid PostSalaryRequest salaryUpdate) throws MyException {

        String error = checkOneSalaryRequestToUpdate(salaryUpdate);
        if (!error.isEmpty())
            throw new MyException(HttpStatus.BAD_REQUEST, error);

        Optional<User> optUser = userService.getOptionalUser(salaryUpdate.getEmployee());
        if (optUser.isEmpty()) {
            throw new MyException(HttpStatus.BAD_REQUEST, "User '" + salaryUpdate.getEmployee() + "' does not exists");
        }

        User user = optUser.get();
        Salary salary = salaryRepository.findByUserAndPeriod(user, salaryUpdate.getPeriod())
                .orElseGet(() -> new Salary(user, salaryUpdate.getPeriod(), 0L));

        salary.setSalary(salaryUpdate.getSalary());

        salaryRepository.save(salary);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new StatusResponse("Updated successfully!"));
    }

    private String checkOneSalaryRequestToUpdate(PostSalaryRequest request) {
        StringBuilder error = new StringBuilder();

        if (request.getSalary() < 0.0) error.append("Negative salary '" + request.getSalary() + "', ");

        if (!request.getPeriod().matches(periodRegexp))
            error.append("Period '" + request.getPeriod() + "' incorrect, ");

        Optional<User> optUser = userService.getOptionalUser(request.getEmployee());
        if (optUser.isEmpty()) {
            error.append("User '" + request.getEmployee() + "' does not exists, ");
        } else {
            if (salaryRepository.findByUserAndPeriod(optUser.get(), request.getPeriod()).isEmpty())
                error.append("User '" + request.getEmployee() + "' has not received salary for '" + request.getPeriod() + "' yet, ");
        }

        return error.toString();
    }

    private String checkListOfSalaries(List<PostSalaryRequest> listOfSalaries) {
        StringBuilder error = new StringBuilder();
        for (PostSalaryRequest request : listOfSalaries) {
            error.append(checkOneSalaryRequest(request));
        }
        return error.toString();
    }

    private String checkOneSalaryRequest(PostSalaryRequest request) {

        StringBuilder error = new StringBuilder();

        if (request.getSalary() < 0.0) error.append("Negative salary '" + request.getSalary() + "', ");

        if (!request.getPeriod().matches(periodRegexp))
            error.append("Period '" + request.getPeriod() + "' incorrect, ");

        Optional<User> optUser = userService.getOptionalUser(request.getEmployee());
        if (optUser.isEmpty()) {
            error.append("User '" + request.getEmployee() + "' does not exists, ");
        } else {
            if (salaryRepository.findByUserAndPeriod(optUser.get(), request.getPeriod()).isPresent())
                error.append("User '" + request.getEmployee() + "' already received salary for '" + request.getPeriod() + "', ");
        }

        return error.toString();
    }
}
