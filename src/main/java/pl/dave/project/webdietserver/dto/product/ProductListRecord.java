package pl.dave.project.webdietserver.dto.product;

import lombok.Data;
import pl.dave.project.webdietserver.dto.NameAndGuid;

@Data
public class ProductListRecord {
    private String guid;
    private String name;
    private long ver;
    private double kcal;
    private NameAndGuid user;
}
