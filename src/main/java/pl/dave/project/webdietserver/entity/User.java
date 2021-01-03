package pl.dave.project.webdietserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.dave.project.webdietserver.entity.enums.UserRole;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractEntity {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private boolean enabled = true;
    private UserRole role = UserRole.USER;

    @Override
    public String toString() {
        return username + " " + firstName + " " + lastName + " " + enabled + " " + role;
    }
}

