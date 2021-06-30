package pl.dave.project.webdietserver.dto.recipe;

import lombok.Data;
import pl.dave.project.webdietserver.entity.enums.RecipeType;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
public class RecipeRequest {

    private String guid;

    @NotNull
    private String name;

    @NotNull
    private RecipeType type;

    @NotNull
    private String description;

    @NotNull
    private Map<String, Long> products = new HashMap<>();
}
