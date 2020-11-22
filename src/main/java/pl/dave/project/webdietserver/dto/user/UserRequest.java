package pl.dave.project.webdietserver.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserRequest {

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String mailHost;
    private Long mailPort;
    private String mailPassword;
}
