package pl.dave.project.webdietserver.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.dave.project.webdietserver.dto.NameAndGuid;
import pl.dave.project.webdietserver.dto.product.ProductListRecord;
import pl.dave.project.webdietserver.dto.product.ProductRequest;
import pl.dave.project.webdietserver.entity.Product;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class ProductMapper {

    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "ver", ignore = true)
    @Mapping(target = "creationTimestamp", ignore = true)
    @Mapping(target = "user", ignore = true)
    public abstract Product toEntity(ProductRequest request);

    public abstract ProductListRecord toListRecord(Product product);

    public abstract List<ProductListRecord> toListRecords(List<Product> product);

    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "ver", ignore = true)
    public abstract Product update(@MappingTarget Product target, Product source);

    public abstract NameAndGuid toNameAndGuid(Product product);
}
