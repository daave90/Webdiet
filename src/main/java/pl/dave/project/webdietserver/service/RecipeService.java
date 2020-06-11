package pl.dave.project.webdietserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.stereotype.Service;
import pl.dave.project.webdietserver.entity.Recipe;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.UserRole;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.RecipeMapper;
import pl.dave.project.webdietserver.repository.RecipeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final ProductService productService;
    private final RecipeMapper mapper;

    public Recipe save(Recipe recipe, User user) {
        log.info("**************************************************************************************************");
        log.info("Saving recipe: " + recipe);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(recipe == null, ErrorCode.SOURCE_ENTITY_NOT_EXISTS);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        recipe.setUser(user);
        if (!isProductsWeightGreaterThanZero(recipe)) {
            throw new RestApiException(ErrorCode.PRODUCTS_WEIGHT_LESS_THAN_ZERO);
        }
        recipe.setTotalKcal(countKcal(recipe, user));
        return recipeRepository.save(recipe);
    }

    private double countKcal(Recipe recipe, User user) {
        if (CollectionUtils.isEmpty(recipe.getProducts().keySet())) {
            return 0.0;
        }

        return recipe.getProducts().keySet().stream()
                .map(productGuid -> productService.getByGuid(productGuid, user))
                .map(product -> (product.getKcal() * recipe.getProducts().get(product.getGuid())) / 100.0)
                .reduce(Double::sum)
                .orElseThrow(() -> new RestApiException(ErrorCode.TOTAL_KCAL_SUM_ERROR));
    }

    private boolean isProductsWeightGreaterThanZero(Recipe recipe) {
        return recipe.getProducts().values().stream()
                .allMatch(count -> count > 0);
    }

    public List<Recipe> list(User user) {
        log.info("**************************************************************************************************");
        log.info("List all recipes");
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        if (user.getRole() == UserRole.ADMIN) {
            return IteratorUtils.toList(recipeRepository.findAll().iterator());
        }
        return IteratorUtils.toList(recipeRepository.findAll().iterator()).stream()
                .filter(recipe -> recipe.getUser().getGuid().equals(user.getGuid()))
                .collect(Collectors.toList());
    }

    public Recipe getByGuid(String guid, User user) {
        log.info("**************************************************************************************************");
        log.info("Get recipe by guid: " + guid);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        RestApiException.throwExceptionIfConditionIsTrue(guid == null, ErrorCode.SOURCE_ENTITY_NOT_EXISTS);
        Recipe recipe = recipeRepository.findById(guid).orElse(null);
        RestApiException.throwExceptionIfConditionIsTrue(recipe == null, ErrorCode.RECIPE_NOT_EXISTS);
        if (user.getRole() == UserRole.ADMIN ||
                (recipe.getUser().getGuid().equals(user.getGuid()))) {
            return recipe;
        }
        throw new RestApiException(ErrorCode.GET_WRONG_RESOURCE);
    }

    public Recipe getByGuid(String guid) {
        return recipeRepository.findById(guid).orElse(new Recipe());
    }

    public Recipe update(String guid, Recipe source, User user) {
        log.info("**************************************************************************************************");
        log.info("Update recipe: " + guid + " source: " + source);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        RestApiException.throwExceptionIfConditionIsTrue(source == null, ErrorCode.SOURCE_ENTITY_NOT_EXISTS);
        Recipe recipeToUpdate = recipeRepository.findById(guid).orElse(null);
        if (recipeToUpdate != null && (user.getRole() == UserRole.ADMIN ||
                (user.getGuid().equals(recipeToUpdate.getUser().getGuid())))) {
            User newUser = recipeToUpdate.getUser();
            recipeToUpdate = mapper.update(recipeToUpdate, source);
            recipeToUpdate.setUser(newUser);
            recipeToUpdate.setProducts(source.getProducts());
            if (!isProductsWeightGreaterThanZero(recipeToUpdate)) {
                throw new RestApiException(ErrorCode.PRODUCTS_WEIGHT_LESS_THAN_ZERO);
            }
            recipeToUpdate.setTotalKcal(countKcal(recipeToUpdate, newUser));
            return recipeRepository.save(recipeToUpdate);
        }
        throw new RestApiException(ErrorCode.UPDATE_WRONG_RESOURCE);
    }

    public void delete(Recipe recipe, User user) {
        log.info("**************************************************************************************************");
        log.info("Remove recipe: " + recipe);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(recipe == null, ErrorCode.RECIPE_NOT_EXISTS);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);

        if (user.getRole() == UserRole.ADMIN ||
                (recipe.getUser().getGuid().equals(user.getGuid()))) {
            recipeRepository.delete(recipe);
        } else {
            throw new RestApiException(ErrorCode.DELETE_WRONG_RESOURCE);
        }
    }
}
