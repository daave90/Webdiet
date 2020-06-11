package pl.dave.project.webdietserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "shoppinglist")
public class ShoppingList extends AbstractEntity {

    private int daysNumber;

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "PRODUCTGUID")
    @Column(name = "WEIGHT")
    @CollectionTable(name = "product_shoppinglist",
            joinColumns = @JoinColumn(name = "SCHOPPINGLISTGUID"))
    private Map<String, Long> productsAndWeight = new HashMap<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "RECIPEGUID")
    @CollectionTable(name = "recipe_shoppinglist", joinColumns = @JoinColumn(name = "SCHOPPINGLISTGUID"))
    private List<String> recipes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "USERGUID")
    private User user;

    @Override
    public String toString() {
        return "ShoppingList: " + guid + " " + daysNumber + " " + user;
    }
}
