package account.DTO;

import lombok.Data;

@Data
public class UserOperationRequest {
    private String user;
    private UserOperation operation;
}
