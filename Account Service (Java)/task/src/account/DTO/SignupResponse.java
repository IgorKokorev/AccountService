package account.DTO;

import account.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SignupResponse {
    private long id;
    private String name;
    private String lastname;
    private String email;
    private List<String> roles;

    public SignupResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getRole())
                .sorted()
                .toList();
    }
}
