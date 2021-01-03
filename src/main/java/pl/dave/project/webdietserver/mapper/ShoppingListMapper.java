package pl.dave.project.webdietserver.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import pl.dave.project.webdietserver.dto.NameAndGuid;
import pl.dave.project.webdietserver.dto.shoppinglist.ShoppingListListRecord;
import pl.dave.project.webdietserver.dto.shoppinglist.ShoppingListRequest;
import pl.dave.project.webdietserver.entity.ShoppingList;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.service.RecipeService;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {RecipeMapper.class, UserMapper.class})
public abstract class ShoppingListMapper {

    @Autowired
    private RecipeService recipeService;

    @Mapping(target = "guid", ignore = true)
    @Mapping(target = "creationTimestamp", ignore = true)
    @Mapping(target = "productsAndWeight", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    public abstract ShoppingList toEntity(ShoppingListRequest request);

    @AfterMapping
    void setRecipes(ShoppingListRequest request, @MappingTarget ShoppingList shoppingList) {
        List<String> recipesGuids = request.getRecipes().stream()
                .map(NameAndGuid::getGuid)
                .collect(Collectors.toList());
        shoppingList.setRecipes(recipesGuids);
    }

    @AfterMapping
    void setProductAndWeight(ShoppingListRequest request, @MappingTarget ShoppingList shoppingList) {
        List<Map.Entry<String, Long>> entries = getProductEntries(request);
        List<String> entryGuids = getProductGuidsFromEntryList(entries);
        Set<String> duplicates = getDuplicateGuidsFromProductGuids(entryGuids);
        Map<String, Long> productsAndWeight = new HashMap<>();

        for (String duplicate : duplicates) {
            Long sum = entries.stream()
                    .filter(entry -> entry.getKey().equals(duplicate))
                    .map(Map.Entry::getValue)
                    .reduce(0L, Long::sum);
            productsAndWeight.put(duplicate, sum);
        }

        entryGuids.removeAll(duplicates);

        for (String nonDuplicate : entryGuids) {
            Long weight = entries.stream()
                    .filter(entry -> entry.getKey().equals(nonDuplicate))
                    .map(Map.Entry::getValue)
                    .findAny().orElse(0L);
            if (weight == 0) {
                throw new RestApiException(ErrorCode.WEIGHT_ERROR);
            }
            productsAndWeight.put(nonDuplicate, weight);
        }

        shoppingList.setProductsAndWeight(productsAndWeight);
    }

    private Set<String> getDuplicateGuidsFromProductGuids(List<String> entryGuids) {
        return entryGuids.stream()
                .filter(guid -> Collections.frequency(entryGuids, guid) > 1)
                .collect(Collectors.toSet());
    }

    private List<String> getProductGuidsFromEntryList(List<Map.Entry<String, Long>> entries) {
        return entries.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<Map.Entry<String, Long>> getProductEntries(ShoppingListRequest request) {
        return request.getRecipes().stream()
                .map(nameAndGuid -> recipeService.getByGuid(nameAndGuid.getGuid()))
                .flatMap(recipe -> recipe.getProducts().entrySet().stream())
                .peek(entry -> entry.setValue(entry.getValue() * request.getDaysNumber()))
                .collect(Collectors.toList());
    }

    public abstract ShoppingListListRecord toListRecord(ShoppingList shoppingList);

    public abstract List<ShoppingListListRecord> toListRecords(List<ShoppingList> shoppingLists);

    @Mapping(target = "guid", ignore = true)
    public abstract ShoppingList update(@MappingTarget ShoppingList target, ShoppingList source);
}
