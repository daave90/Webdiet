package pl.dave.project.webdietserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class ShoppingList extends AbstractEntity {

    private int daysNumber;
    private Map<String, Long> productsAndWeight = new HashMap<>();
    private List<String> recipes = new ArrayList<>();
    private User user;

    @Override
    public String toString() {
        return "ShoppingList: " + guid + " " + daysNumber + " " + user;
    }
}
