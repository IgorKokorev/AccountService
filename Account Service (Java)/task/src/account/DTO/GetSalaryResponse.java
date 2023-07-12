package account.DTO;

import account.model.Salary;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class GetSalaryResponse {
    @JsonIgnore
    private final String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    private String name;
    private String lastname;
    private String period;
    private String salary;

    public GetSalaryResponse(Salary salary) {
        this.name = salary.getUser().getName();
        this.lastname = salary.getUser().getLastname();
        this.period = months[salary.getMonth() - 1] + "-" + salary.getYear();
        this.salary = "" + (salary.getSalary() / 100) + " dollar(s) " + (salary.getSalary() % 100) + " cent(s)";
    }
}
