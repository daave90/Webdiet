package pl.dave.project.webdietserver.dto.user;

import lombok.Data;

@Data
public class UserListRecord {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private String role;
    private String mailHost;
    private Long mailPort;
    private String mailPassword;
}
