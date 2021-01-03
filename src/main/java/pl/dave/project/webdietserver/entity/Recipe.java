package pl.dave.project.webdietserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.dave.project.webdietserver.entity.enums.RecipeType;

import java.util.HashMap;
import java.util.Map;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Recipe extends AbstractEntity {

    private String name;
    private double totalKcal;
    private RecipeType type;
    private String description;
    private Map<String, Long> products = new HashMap<>();
    private User user;

    @Override
    public String toString() {
        return name + " " + totalKcal + " kcal " + type;
    }
}
