package pl.dave.project.webdietserver.dto.shoppinglist;

import lombok.Data;
import pl.dave.project.webdietserver.dto.NameAndGuid;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
public class ShoppingListRequest {

    private String guid;

    @NotNull
    private int daysNumber;
    private Set<NameAndGuid> recipes = new HashSet<>();
}
