package pl.dave.project.webdietserver.dto.shoppinglist;

import lombok.Data;
import pl.dave.project.webdietserver.dto.NameAndGuid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ShoppingListListRecord {
    private String guid;
    private long ver;
    private int daysNumber;
    private Map<String, Long> productsAndWeight = new HashMap<>();
    private List<String> recipes = new ArrayList<>();
    private NameAndGuid user;
}
