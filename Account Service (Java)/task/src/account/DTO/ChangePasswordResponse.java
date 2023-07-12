package account.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordResponse {
    private String email;
    private String status;

    public ChangePasswordResponse(String email) {
        this.email = email;
        this.status = "The password has been updated successfully";
    }
}
