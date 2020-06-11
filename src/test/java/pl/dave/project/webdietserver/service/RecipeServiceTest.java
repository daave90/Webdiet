package pl.dave.project.webdietserver.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.dave.project.webdietserver.entity.AbstractEntity;
import pl.dave.project.webdietserver.entity.Product;
import pl.dave.project.webdietserver.entity.Recipe;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.RecipeType;
import pl.dave.project.webdietserver.entity.enums.UserRole;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.RecipeMapper;
import pl.dave.project.webdietserver.repository.RecipeRepository;
import pl.dave.project.webdietserver.util.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @InjectMocks
    private static RecipeService service;
    private static List<Recipe> recipes;
    private static List<Product> products;
    private static User admin;
    private static User user;

    @BeforeAll
    static void init() {
        admin = TestUtils.createUser("admin", "The", "Admin", "admin", UserRole.ADMIN);
        user = TestUtils.createUser("dmaciak", "Dawid", "Maciak", "dmaciak", UserRole.USER);

        recipes = new ArrayList<>();
        products = new ArrayList<>();
        RecipeRepository repository = mock(RecipeRepository.class);
        RecipeMapper mapper = mock(RecipeMapper.class);
        ProductService productService = mock(ProductService.class);

        doAnswer(invocationOnMock -> recipes)
                .when(repository).findAll();

        doAnswer(invocationOnMock -> {
            String guid = invocationOnMock.getArgument(0);
            return recipes.stream()
                    .filter(recipe -> recipe.getGuid().equals(guid))
                    .findFirst();
        }).when(repository).findById(anyString());

        doAnswer(invocationOnMock -> {
            Recipe recipe = invocationOnMock.getArgument(0);
            boolean contains = recipes.stream()
                    .map(AbstractEntity::getGuid)
                    .anyMatch(guid -> guid.equals(recipe.getGuid()));
            if (contains) {
                Recipe recipeToDelete = recipes.stream()
                        .filter(recipe1 -> recipe1.getGuid().equals(recipe.getGuid()))
                        .findFirst().orElse(new Recipe());
                recipes.remove(recipeToDelete);
            }
            recipes.add(recipe);
            return recipe;
        }).when(repository).save(any(Recipe.class));

        doAnswer(invocationOnMock -> recipes.remove(invocationOnMock.getArgument(0)))
                .when(repository).delete(any(Recipe.class));

        doAnswer(invocationOnMock -> {
            Recipe target = invocationOnMock.getArgument(0);
            Recipe source = invocationOnMock.getArgument(1);
            source.setGuid(target.getGuid());
            return source;
        }).when(mapper).update(any(Recipe.class), any(Recipe.class));

        service = new RecipeService(repository, productService, mapper);

        doAnswer(invocationOnMock -> {
            Product result = products.stream()
                    .filter(product -> product.getGuid().equals(invocationOnMock.getArgument(0)))
                    .findFirst().orElse(new Product());
            if (result.getUser() == invocationOnMock.getArgument(1)) {
                return result;
            }
            throw new RestApiException(ErrorCode.GET_WRONG_RESOURCE);
        }).when(productService).getByGuid(anyString(), any(User.class));
    }

    @AfterEach
    void clearData() {
        products.clear();
        recipes.clear();
    }

    private Map<String, Long> mapProductsListIntoRecipeCollection(List<Product> products, User user) {
        return products.stream()
                .filter(product -> product.getUser() == user)
                .collect(Collectors.toMap(Product::getGuid, weight -> 100L));
    }

    private Recipe createSourceRecipeToUpdate(User user) {
        Recipe recipe = new Recipe();
        recipe.setName("update");
        recipe.setUser(user);
        recipe.setType(RecipeType.SNIADANIE);
        return recipe;
    }

    private List<Recipe> createRecipes() {
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();
        Recipe recipe3 = new Recipe();
        Recipe recipe4 = new Recipe();

        recipe1.setName("recipe1");
        recipe1.setType(RecipeType.SNIADANIE);
        recipe1.setDescription("desc1");
        recipe1.setTotalKcal(0);
        recipe1.setUser(user);

        recipe2.setName("recipe2");
        recipe2.setType(RecipeType.DRUGIE_SNIADANIE);
        recipe2.setDescription("desc2");
        recipe2.setTotalKcal(200.0);
        recipe2.setUser(user);
        recipe2.setProducts(mapProductsListIntoRecipeCollection(TestUtils.createProducts(user, admin), user));

        recipe3.setName("recipe3");
        recipe3.setType(RecipeType.OBIAD);
        recipe3.setDescription("desc3");
        recipe3.setTotalKcal(0);
        recipe3.setUser(admin);

        recipe4.setName("recipe4");
        recipe4.setType(RecipeType.KOLACJA);
        recipe4.setDescription("desc4");
        recipe4.setTotalKcal(300.0);
        recipe4.setUser(admin);
        recipe4.setProducts(mapProductsListIntoRecipeCollection(TestUtils.createProducts(user, admin), admin));

        return new ArrayList<>(Arrays.asList(recipe1, recipe2, recipe3, recipe4));
    }

    @Test
    void createRecipeByUser() {
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();
        products = TestUtils.createProducts(admin, user);

        recipe1.setName("Przepis1");
        recipe1.setDescription("desc");
        recipe1.setType(RecipeType.OBIAD);
        service.save(recipe1, user);

        recipe2.setName("Przepis2");
        recipe2.setDescription("desc2");
        recipe2.setType(RecipeType.DRUGIE_SNIADANIE);
        recipe2.setProducts(mapProductsListIntoRecipeCollection(products, user));
        service.save(recipe2, user);

        assertThat(recipes, hasSize(2));
        assertThat(recipes, Matchers.contains(recipe1, recipe2));
        assertSame(recipes.get(0).getUser(), user);
        assertEquals("Przepis1", recipes.get(0).getName());
        assertEquals("desc", recipes.get(0).getDescription());
        assertEquals(RecipeType.OBIAD, recipes.get(0).getType());
        assertThat(recipes.get(0).getProducts().keySet(), empty());

        assertSame(recipes.get(1).getUser(), user);
        assertEquals("Przepis2", recipes.get(1).getName());
        assertEquals("desc2", recipes.get(1).getDescription());
        assertEquals(RecipeType.DRUGIE_SNIADANIE, recipes.get(1).getType());
        assertThat(recipes.get(1).getProducts().keySet(), hasSize(2));
        assertEquals(200.0, recipes.get(1).getTotalKcal());
    }

    @Test
    void createRecipeByAdmin() {
        Recipe recipe1 = new Recipe();
        Recipe recipe2 = new Recipe();
        products = TestUtils.createProducts(admin, user);

        recipe1.setName("Przepis1");
        recipe1.setDescription("desc");
        recipe1.setType(RecipeType.OBIAD);
        service.save(recipe1, admin);

        recipe2.setName("Przepis2");
        recipe2.setDescription("desc2");
        recipe2.setType(RecipeType.DRUGIE_SNIADANIE);
        recipe2.setProducts(mapProductsListIntoRecipeCollection(products, admin));
        service.save(recipe2, admin);

        assertThat(recipes, hasSize(2));
        assertThat(recipes, Matchers.contains(recipe1, recipe2));
        assertSame(recipes.get(0).getUser(), admin);
        assertEquals("Przepis1", recipes.get(0).getName());
        assertEquals("desc", recipes.get(0).getDescription());
        assertEquals(RecipeType.OBIAD, recipes.get(0).getType());
        assertThat(recipes.get(0).getProducts().keySet(), empty());

        assertSame(recipes.get(1).getUser(), admin);
        assertEquals("Przepis2", recipes.get(1).getName());
        assertEquals("desc2", recipes.get(1).getDescription());
        assertEquals(RecipeType.DRUGIE_SNIADANIE, recipes.get(1).getType());
        assertThat(recipes.get(1).getProducts().keySet(), hasSize(2));
        assertEquals(300.0, recipes.get(1).getTotalKcal());
    }

    @Test
    void createRecipeByUserWithProductWhichNotBelongsToUser() {
        Recipe recipe = new Recipe();
        recipe.setName("Przepis1");
        recipe.setDescription("desc");
        recipe.setType(RecipeType.OBIAD);
        recipe.setProducts(mapProductsListIntoRecipeCollection(TestUtils.createProducts(user, admin), admin));
        assertThrows(RestApiException.class, () -> service.save(recipe, user));
    }

    @Test
    void createRecipewithNulls() {
        assertThrows(RestApiException.class, () -> service.save(null, admin));
        assertThrows(RestApiException.class, () -> service.save(null, user));
        assertThrows(RestApiException.class, () -> service.save(null, null));
        assertThrows(RestApiException.class, () -> service.save(new Recipe(), null));
    }

    @Test
    void getRecipeByUser() {
        recipes = createRecipes();
        Recipe result = service.getByGuid(recipes.get(0).getGuid(), user);

        assertEquals("recipe1", result.getName());
        assertEquals(0.0, result.getTotalKcal());
        assertEquals(RecipeType.SNIADANIE, result.getType());
    }

    @Test
    void getRecipeWhichNotBelongsToUserByUser() {
        recipes = createRecipes();
        assertThrows(RestApiException.class, () -> service.getByGuid(recipes.get(2).getGuid(), user));
    }

    @Test
    void getRecipeByAdmin() {
        recipes = createRecipes();
        Recipe result = service.getByGuid(recipes.get(2).getGuid(), admin);

        assertEquals("recipe3", result.getName());
        assertEquals(0.0, result.getTotalKcal());
        assertEquals(RecipeType.OBIAD, result.getType());
    }

    @Test
    void getRecipeByAdminWhichNotBelongsToAdmin() {
        recipes = createRecipes();
        Recipe result = service.getByGuid(recipes.get(0).getGuid(), admin);

        assertEquals("recipe1", result.getName());
        assertEquals(0.0, result.getTotalKcal());
        assertSame(result.getUser(), user);
        assertEquals(RecipeType.SNIADANIE, result.getType());
    }

    @Test
    void getNotExistingRecipe() {
        recipes = createRecipes();
        assertThrows(RestApiException.class, () -> service.getByGuid(null, user));
        assertThrows(RestApiException.class, () -> service.getByGuid(null, admin));
        assertThrows(RestApiException.class, () -> service.getByGuid("null", user));
        assertThrows(RestApiException.class, () -> service.getByGuid("null", admin));
        assertThrows(RestApiException.class, () -> service.getByGuid("null", null));
    }

    @Test
    void getAllByUser() {
        recipes = createRecipes();
        List<Recipe> result = service.list(user);

        assertThat(result, hasSize(2));
    }

    @Test
    void getAllByAdmin() {
        recipes = createRecipes();
        List<Recipe> result = service.list(admin);

        assertThat(result, hasSize(4));
    }

    @Test
    void getAllByNullUser() {
        assertThrows(RestApiException.class, () -> service.list(null));
    }

    @Test
    void deleteRecipeByUser() {
        recipes = createRecipes();
        service.delete(recipes.get(0), user);
        assertThat(recipes, hasSize(3));
    }

    @Test
    void deleteRecipeByUserWhichNotBelongsToUser() {
        recipes = createRecipes();
        assertThrows(RestApiException.class, () -> service.delete(recipes.get(2), user));
    }

    @Test
    void deleteRecipeByAdmin() {
        recipes = createRecipes();
        service.delete(recipes.get(2), admin);
        assertThat(recipes, hasSize(3));
    }

    @Test
    void deleteRecipeByAdminWhichNotBelongsToAdmin() {
        recipes = createRecipes();
        service.delete(recipes.get(0), admin);
        assertThat(recipes, hasSize(3));
    }

    @Test
    void deleteRecipeWithNulls() {
        recipes = createRecipes();
        assertThrows(RestApiException.class, () -> service.delete(null, user));
        assertThrows(RestApiException.class, () -> service.delete(null, admin));
        assertThrows(RestApiException.class, () -> service.delete(null, null));
        assertThrows(RestApiException.class, () -> service.delete(recipes.get(0), null));
    }

    @Test
    void updateRecipeByUser() {
        recipes = createRecipes();
        String guid = recipes.get(1).getGuid();
        service.update(guid, createSourceRecipeToUpdate(user), user);
        Recipe result = recipes.stream()
                .filter(recipe -> recipe.getGuid().equals(guid))
                .findFirst().get();
        assertThat(recipes, hasSize(4));
        assertSame(result.getUser(), user);
        assertThat(result.getName(), equalTo("update"));
        assertThat(result.getType(), equalTo(RecipeType.SNIADANIE));
        assertThat(result.getTotalKcal(), equalTo(0.0));

        products = TestUtils.createProducts(admin, user);
        String guid2 = recipes.get(0).getGuid();
        Recipe source = createSourceRecipeToUpdate(user);
        source.setProducts(mapProductsListIntoRecipeCollection(products, user));
        service.update(guid2, source, user);
        result = recipes.stream()
                .filter(recipe -> recipe.getGuid().equals(guid2))
                .findFirst().get();
        assertThat(recipes, hasSize(4));
        assertSame(result.getUser(), user);
        assertThat(result.getName(), equalTo("update"));
        assertThat(result.getType(), equalTo(RecipeType.SNIADANIE));
        assertThat(result.getTotalKcal(), equalTo(200.0));
        assertThat(result.getProducts().keySet(), hasSize(2));
    }

    @Test
    void updateRecipeByAdmin(){
        recipes = createRecipes();
        String guid = recipes.get(2).getGuid();
        service.update(guid, createSourceRecipeToUpdate(admin), admin);
        Recipe result = recipes.stream()
                .filter(recipe -> recipe.getGuid().equals(guid))
                .findFirst().get();
        assertThat(recipes, hasSize(4));
        assertSame(result.getUser(), admin);
        assertThat(result.getName(), equalTo("update"));
        assertThat(result.getType(), equalTo(RecipeType.SNIADANIE));
        assertThat(result.getTotalKcal(), equalTo(0.0));
    }

    @Test
    void updateRecipeWhichNotBelongsToAdminByAdmin(){
        recipes = createRecipes();
        String guid = recipes.get(0).getGuid();
        service.update(guid, createSourceRecipeToUpdate(user), admin);
        Recipe result = recipes.stream()
                .filter(recipe -> recipe.getGuid().equals(guid))
                .findFirst().get();
        assertThat(recipes, hasSize(4));
        assertSame(result.getUser(), user);
        assertThat(result.getName(), equalTo("update"));
        assertThat(result.getType(), equalTo(RecipeType.SNIADANIE));
        assertThat(result.getTotalKcal(), equalTo(0.0));
    }

    @Test
    void updateRecipeWhichNotBelongsToUserByUser() {
        recipes = createRecipes();
        assertThrows(RestApiException.class, () -> service.update(recipes.get(3).getGuid(), createSourceRecipeToUpdate(user), user));
    }

    @Test
    void updateRecipesWithNullValues() {
        recipes = createRecipes();
        products = TestUtils.createProducts(admin, user);
        assertThrows(RestApiException.class, () -> service.update(recipes.get(0).getGuid(), null, user));
        assertThrows(RestApiException.class, () -> service.update(recipes.get(0).getGuid(), createSourceRecipeToUpdate(admin), null));
        assertThrows(RestApiException.class, () -> service.update(null, createSourceRecipeToUpdate(admin), admin));
        assertThrows(RestApiException.class, () -> service.update("recipes.get(0).getGuid()", createSourceRecipeToUpdate(admin), admin));
        assertThrows(RestApiException.class, () -> service.update(null, createSourceRecipeToUpdate(user), user));
        assertThrows(RestApiException.class, () -> service.update("recipes.get(0).getGuid()", createSourceRecipeToUpdate(user), user));
    }
}
