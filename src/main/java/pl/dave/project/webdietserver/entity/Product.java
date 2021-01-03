package pl.dave.project.webdietserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Product extends AbstractEntity {
    private String name;
    private double kcal;
    private User user;

    @Override
    public String toString() {
        return name + " " + kcal + " kcal " + user;
    }
}
