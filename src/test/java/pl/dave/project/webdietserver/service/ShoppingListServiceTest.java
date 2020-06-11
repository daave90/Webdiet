package pl.dave.project.webdietserver.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import pl.dave.project.webdietserver.entity.AbstractEntity;
import pl.dave.project.webdietserver.entity.Product;
import pl.dave.project.webdietserver.entity.ShoppingList;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.UserRole;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.ShoppingListMapper;
import pl.dave.project.webdietserver.repository.ShoppingListRepository;
import pl.dave.project.webdietserver.util.TestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class ShoppingListServiceTest {

    @InjectMocks
    private static ShoppingListService service;
    private static List<ShoppingList> shoppingLists;
    private static User user;
    private static User admin;

    @BeforeAll
    static void initialize() {
        ShoppingListRepository repository = mock(ShoppingListRepository.class);
        ShoppingListMapper mapper = mock(ShoppingListMapper.class);
        admin = TestUtils.createUser("admin", "The", "Admin", "admin", UserRole.ADMIN);
        user = TestUtils.createUser("dmaciak", "Dawid", "Maciak", "dmaciak", UserRole.USER);
        shoppingLists = new ArrayList<>();

        doAnswer(invocationOnMock -> shoppingLists)
                .when(repository).findAll();

        doAnswer(invocationOnMock -> {
            String guid = invocationOnMock.getArgument(0);
            return shoppingLists.stream()
                    .filter(shoppingList -> shoppingList.getGuid().equals(guid))
                    .findFirst();
        }).when(repository).findById(anyString());

        doAnswer(invocationOnMock -> {
            ShoppingList shoppingList = invocationOnMock.getArgument(0);
            boolean contains = shoppingLists.stream()
                    .map(AbstractEntity::getGuid)
                    .anyMatch(guid -> guid.equals(shoppingList.getGuid()));
            if (contains) {
                ShoppingList shoppingListToDelete = shoppingLists.stream()
                        .filter(shoppingList1 -> shoppingList1.getGuid().equals(shoppingList.getGuid()))
                        .findFirst().orElse(new ShoppingList());
                shoppingLists.remove(shoppingListToDelete);
            }
            shoppingLists.add(shoppingList);
            return shoppingList;
        }).when(repository).save(any(ShoppingList.class));

        doAnswer(invocationOnMock -> shoppingLists.remove(invocationOnMock.getArgument(0)))
                .when(repository).delete(any(ShoppingList.class));

        doAnswer(invocationOnMock -> {
            ShoppingList target = invocationOnMock.getArgument(0);
            ShoppingList source = invocationOnMock.getArgument(1);
            source.setGuid(target.getGuid());
            return source;
        }).when(mapper).update(any(ShoppingList.class), any(ShoppingList.class));

        service = new ShoppingListService(repository, mapper);
    }

    @AfterEach
    void cleanup() {
        shoppingLists.clear();
    }

    private List<ShoppingList> createShoppingLists() {
        List<ShoppingList> shoppingLists = new ArrayList<>();
        List<Product> products = TestUtils.createProducts(admin, user);
        Map<String, Long> productsAndWeight = new HashMap<>();
        productsAndWeight.put(products.get(0).getGuid(), 1000L);
        productsAndWeight.put(products.get(1).getGuid(), 200L);

        ShoppingList shoppingList1 = new ShoppingList();
        shoppingList1.setUser(admin);
        shoppingList1.setDaysNumber(1);

        ShoppingList shoppingList2 = new ShoppingList();
        shoppingList2.setUser(admin);
        shoppingList2.setDaysNumber(2);
        shoppingList2.setProductsAndWeight(productsAndWeight);

        ShoppingList shoppingList3 = new ShoppingList();
        shoppingList3.setUser(user);
        shoppingList3.setDaysNumber(3);

        ShoppingList shoppingList4 = new ShoppingList();
        shoppingList4.setUser(user);
        shoppingList4.setDaysNumber(4);
        productsAndWeight.clear();
        productsAndWeight.put(products.get(2).getGuid(), 1000L);
        productsAndWeight.put(products.get(3).getGuid(), 200L);
        shoppingList4.setProductsAndWeight(productsAndWeight);

        shoppingLists.add(shoppingList1);
        shoppingLists.add(shoppingList2);
        shoppingLists.add(shoppingList3);
        shoppingLists.add(shoppingList4);

        return shoppingLists;
    }

    @Test
    void createShoppingListByUser() {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setDaysNumber(3);

        service.save(shoppingList, user);

        assertNotNull(shoppingList.getProductsAndWeight());
        assertEquals(shoppingList.getUser(), user);
        assertEquals(shoppingList.getDaysNumber(), 3);

        List<Product> products = TestUtils.createProducts(admin, user);
        ShoppingList shoppingList1 = new ShoppingList();
        Map<String, Long> productsAndWeight = new HashMap<>();
        productsAndWeight.put(products.get(2).getGuid(), 1000L);
        productsAndWeight.put(products.get(3).getGuid(), 200L);
        shoppingList1.setDaysNumber(3);
        shoppingList1.setProductsAndWeight(productsAndWeight);

        service.save(shoppingList1, user);

        assertThat(shoppingLists, hasSize(2));
        assertEquals(shoppingList1.getProductsAndWeight(), productsAndWeight);
        assertEquals(shoppingList1.getUser(), user);
        assertEquals(shoppingList1.getDaysNumber(), 3);
    }

    @Test
    void createForNullUserAndCreateNullShoppingList() {
        assertThrows(RestApiException.class, () -> service.save(null, user));
        assertThrows(RestApiException.class, () -> service.save(new ShoppingList(), null));
        assertThrows(RestApiException.class, () -> service.save(null, null));
    }

    @Test
    void getShoppingListsByAdmin() {
        shoppingLists = createShoppingLists();
        List<ShoppingList> shoppingLists = service.list(admin);
        assertThat(shoppingLists, hasSize(4));
    }

    @Test
    void getShoppingListsByUser() {
        shoppingLists = createShoppingLists();
        List<ShoppingList> shoppingLists = service.list(user);
        assertThat(shoppingLists, hasSize(2));
    }

    @Test
    void getShoppingListsByNull() {
        assertThrows(RestApiException.class, () -> service.list(null));
    }

    @Test
    void getByUser() {
        shoppingLists = createShoppingLists();
        ShoppingList result = service.getByGuid(shoppingLists.get(2).getGuid(), user);
        assertEquals(shoppingLists.get(2), result);

        assertThrows(RestApiException.class, () -> service.getByGuid(shoppingLists.get(0).getGuid(), user));
    }

    @Test
    void getByAdmin() {
        shoppingLists = createShoppingLists();
        ShoppingList result = service.getByGuid(shoppingLists.get(2).getGuid(), admin);
        assertEquals(shoppingLists.get(2), result);

        result = service.getByGuid(shoppingLists.get(0).getGuid(), admin);
        assertEquals(shoppingLists.get(0), result);
    }

    @Test
    void getNotExistShoppingList() {
        assertThrows(RestApiException.class, () -> service.getByGuid("asd", admin));
        assertThrows(RestApiException.class, () -> service.getByGuid("asd", user));
    }

    @Test
    void getNullShoppingList() {
        shoppingLists = createShoppingLists();
        assertThrows(RestApiException.class, () -> service.getByGuid(null, null));
        assertThrows(RestApiException.class, () -> service.getByGuid(null, admin));
        assertThrows(RestApiException.class, () -> service.getByGuid(null, user));
        assertThrows(RestApiException.class, () -> service.getByGuid(shoppingLists.get(0).getGuid(), null));
    }
}
