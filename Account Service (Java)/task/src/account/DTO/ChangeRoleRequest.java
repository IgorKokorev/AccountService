package account.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ChangeRoleRequest {
    public static enum RoleOperation {GRANT, REMOVE};

    private String user;
    private String role;
    private RoleOperation operation;
}
