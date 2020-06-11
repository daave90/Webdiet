package pl.dave.project.webdietserver.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserRequest {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
}
