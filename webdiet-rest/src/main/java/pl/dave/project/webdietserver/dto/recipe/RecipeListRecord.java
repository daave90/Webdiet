package pl.dave.project.webdietserver.dto.recipe;

import lombok.Data;
import pl.dave.project.webdietserver.dto.NameAndGuid;
import pl.dave.project.webdietserver.entity.enums.RecipeType;

import java.util.HashMap;
import java.util.Map;

@Data
public class RecipeListRecord {
    private String guid;
    private long ver;
    private String name;
    private double totalKcal;
    private RecipeType type;
    private String description;
    private Map<String, Long> products = new HashMap<>();
    private NameAndGuid user;
}
