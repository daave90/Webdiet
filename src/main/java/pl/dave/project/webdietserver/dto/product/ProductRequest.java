package pl.dave.project.webdietserver.dto.product;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProductRequest {

    @NotNull
    private String name;

    @NotNull
    private double kcal;
}
