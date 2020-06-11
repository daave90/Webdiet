package pl.dave.project.webdietserver.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.dave.project.webdietserver.entity.enums.RecipeType;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "recipe")
public class Recipe extends AbstractEntity {

    private String name;

    private double totalKcal;

    @Enumerated(EnumType.STRING)
    private RecipeType type;

    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @MapKeyColumn(name = "PRODUCTGUID")
    @Column(name = "WEIGHT")
    @CollectionTable(name = "product_recipe",
            joinColumns = @JoinColumn(name = "RECIPEGUID"))
    private Map<String, Long> products = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "USERGUID")
    private User user;

    @Override
    public String toString() {
        return name + " " + totalKcal + " kcal " + type;
    }
}
