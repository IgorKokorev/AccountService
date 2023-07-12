package account.DTO;

import account.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostSalaryRequest {
    @NotBlank
    private String employee;
    @NotBlank
    private String period;
    @NotNull
    private Long salary;
}
