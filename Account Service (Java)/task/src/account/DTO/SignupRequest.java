package account.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String lastname;
    @NotNull
    private String email;
    @NotNull
    private String password;

}
