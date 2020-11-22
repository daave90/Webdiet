package pl.dave.project.webdietserver.entity;

import lombok.Getter;
import lombok.Setter;
import pl.dave.project.webdietserver.entity.enums.UserRole;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "user")
public class User extends AbstractEntity {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean enabled = true;
    private String mailHost;
    private long mailPort;
    private String mailPassword;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @Override
    public String toString() {
        return email + " " + firstName + " " + lastName + " " + enabled + " " + role;
    }
}

