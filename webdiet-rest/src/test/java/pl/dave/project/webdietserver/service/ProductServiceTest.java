package pl.dave.project.webdietserver.service;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.dave.project.webdietserver.entity.AbstractEntity;
import pl.dave.project.webdietserver.entity.Product;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.UserRole;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.ProductMapper;
import pl.dave.project.webdietserver.repository.ProductRepository;
import pl.dave.project.webdietserver.util.TestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private static ProductService service;
    private static List<Product> products;
    private static User admin;
    private static User user;

    @BeforeAll
    static void init() {
        admin = TestUtils.createUser("admin", "The", "Admin", "admin", UserRole.ADMIN);
        user = TestUtils.createUser("dmaciak", "Dawid", "Maciak", "dmaciak", UserRole.USER);
        products = new ArrayList<>();

        ProductRepository repository = Mockito.mock(ProductRepository.class);
        ProductMapper mapper = Mockito.mock(ProductMapper.class);

        doAnswer(invocationOnMock -> products)
                .when(repository).findAll();

        doAnswer(invocationOnMock -> {
            String guid = invocationOnMock.getArgument(0);
            return products.stream()
                    .filter(product -> product.getGuid().equals(guid))
                    .findFirst();
        }).when(repository).findById(anyString());

        doAnswer(invocationOnMock -> {
            Product product = invocationOnMock.getArgument(0);
            boolean contains = products.stream()
                    .map(AbstractEntity::getGuid)
                    .anyMatch(guid -> guid.equals(product.getGuid()));
            if (contains) {
                Product productToDelete = products.stream()
                        .filter(product1 -> product1.getGuid().equals(product.getGuid()))
                        .findFirst().orElse(new Product());
                products.remove(productToDelete);
            }
            products.add(product);
            return product;
        }).when(repository).save(any(Product.class));

        doAnswer(invocationOnMock -> products.remove(invocationOnMock.getArgument(0)))
                .when(repository).delete(any(Product.class));

        doAnswer(invocationOnMock -> {
            Product target = invocationOnMock.getArgument(0);
            Product source = invocationOnMock.getArgument(1);
            source.setGuid(target.getGuid());
            return source;
        }).when(mapper).update(any(Product.class), any(Product.class));

        service = new ProductService(repository, mapper);
    }

    @AfterEach
    void clearProducts() {
        products.clear();
    }

    private Product createProductSourceToUpdate(User user){
        Product source = new Product();
        source.setName("update");
        source.setKcal(1000);
        source.setUser(user);
        return source;
    }

    @Test
    void createProductsForAdmin() {
        Product product1 = new Product();
        product1.setName("Kalafior");
        product1.setKcal(100);

        Product product2 = new Product();
        product2.setName("Ogórki");
        product2.setKcal(200);

        service.save(product1, admin);
        service.save(product2, admin);

        assertThat(products, hasSize(2));
        assertThat(products, Matchers.contains(product1, product2));
        assertSame(products.get(0).getUser(), admin);
        assertSame(products.get(1).getUser(), admin);
    }

    @Test
    void createProductsForUser() {
        Product product1 = new Product();
        product1.setName("Pomidory");
        product1.setKcal(50);

        Product product2 = new Product();
        product2.setName("Brokuły");
        product2.setKcal(150);

        service.save(product1, user);
        service.save(product2, user);

        assertThat(products, hasSize(2));
        assertThat(products, Matchers.contains(product1, product2));
        assertSame(products.get(0).getUser(), user);
        assertSame(products.get(1).getUser(), user);
    }

    @Test
    void createProductForNullProductAndNullUser() {
        assertThrows(RestApiException.class, () -> service.save(null, null));
        assertThrows(RestApiException.class, () -> service.save(new Product(), null));
        assertThrows(NullPointerException.class, () -> service.save(null, admin));
    }

    @Test
    void getProductsByAdmin() {
        products = TestUtils.createProducts(admin, user);
        List<Product> adminProducts = service.list(admin);

        assertThat(adminProducts, Matchers.not(Matchers.empty()));
        assertThat(adminProducts, hasSize(4));
        assertThat(products.get(0), Matchers.equalTo(adminProducts.get(0)));
        assertThat(products.get(1), Matchers.equalTo(adminProducts.get(1)));
        assertThat(products.get(2), Matchers.equalTo(adminProducts.get(2)));
        assertThat(products.get(3), Matchers.equalTo(adminProducts.get(3)));
    }

    @Test
    void getProductsByUser(){
        products = TestUtils.createProducts(admin, user);
        List<Product> userProducts = service.list(user);

        assertThat(userProducts, Matchers.not(Matchers.empty()));
        assertThat(userProducts, hasSize(2));
        assertThat(products.get(2), Matchers.equalTo(userProducts.get(0)));
        assertThat(products.get(3), Matchers.equalTo(userProducts.get(1)));
    }

    @Test
    void getProductForNullUser(){
        products = TestUtils.createProducts(admin, user);
        assertThrows(RestApiException.class, () -> service.list(null));
    }

    @Test
    void getProductByAdmin(){
        products = TestUtils.createProducts(admin, user);
        Product product = service.getByGuid(products.get(0).getGuid(), admin);
        assertSame(product, products.get(0));
    }

    @Test
    void getProductByAdminWhichBelongsToUser(){
        products = TestUtils.createProducts(admin, user);
        Product product = service.getByGuid(products.get(2).getGuid(), admin);
        assertSame(product.getUser(), user);
        assertSame(product, products.get(2));
    }

    @Test
    void getProductByUserWhichBelongsToUser(){
        products = TestUtils.createProducts(admin, user);
        Product product = service.getByGuid(products.get(2).getGuid(), user);
        assertSame(product.getUser(), user);
        assertSame(product, products.get(2));
    }

    @Test
    void getProductByUserWhichBelongsToAdmin(){
        products = TestUtils.createProducts(admin, user);
        assertThrows(RestApiException.class, () -> service.getByGuid(products.get(0).getGuid(), user));
    }

    @Test
    void getProductWhichNotExists(){
        products = TestUtils.createProducts(admin, user);
        assertThrows(RestApiException.class, () -> service.getByGuid("aaa", admin));
        assertThrows(RestApiException.class, () -> service.getByGuid("aaa", user));
    }

    @Test
    void deleteProductByAdmin(){
        products = TestUtils.createProducts(admin, user);
        service.delete(products.get(0), admin);
        assertThat(products, hasSize(3));
    }

    @Test
    void deleteProductWhichBelongsToUserByAdmin(){
        products = TestUtils.createProducts(admin, user);
        service.delete(products.get(3), admin);
        assertThat(products, hasSize(3));
    }

    @Test
    void deleteProductByUser(){
        products = TestUtils.createProducts(admin, user);
        service.delete(products.get(3), user);
        assertThat(products, hasSize(3));
    }

    @Test
    void deleteProductWhichBelongsToAdminByUser(){
        products = TestUtils.createProducts(admin, user);
        assertThrows(RestApiException.class, () -> service.delete(products.get(0), user));
    }

    @Test
    void deleteNullProduct(){
        products = TestUtils.createProducts(admin, user);
        assertThrows(RestApiException.class, () -> service.delete(null, user));
        assertThrows(RestApiException.class, () -> service.delete(null, admin));
        assertThrows(RestApiException.class, () -> service.delete(null, null));
        assertThrows(RestApiException.class, () -> service.delete(products.get(0), null));
    }

    @Test
    void updateProductwhichBelongsToUserByUser(){
        products = TestUtils.createProducts(admin, user);
        Product source = createProductSourceToUpdate(user);
        String guid = products.get(2).getGuid();

        service.update(guid, source, user);

        Product result = products.stream()
                .filter(product -> product.getGuid().equals(guid))
                .findFirst().get();

        assertThat(products, hasSize(4));
        assertSame(result.getUser(), user);
        assertThat(result.getKcal(), equalTo(1000.0));
        assertThat(result.getName(), equalTo("update"));
    }

    @Test
    void updateProductwhichBelongsToAdminByUser(){
        products = TestUtils.createProducts(admin, user);
        Product source = createProductSourceToUpdate(user);
        assertThrows(RestApiException.class, () -> service.update(products.get(0).getGuid(), source, user));
    }

    @Test
    void updateProductwhichBelongsToUserByAdmin(){
        products = TestUtils.createProducts(admin, user);
        Product source = createProductSourceToUpdate(admin);
        String guid = products.get(2).getGuid();

        service.update(guid, source, admin);

        Product result = products.stream()
                .filter(product -> product.getGuid().equals(guid))
                .findFirst().get();

        assertThat(products, hasSize(4));
        assertSame(result.getUser(), user);
        assertThat(result.getKcal(), equalTo(1000.0));
        assertThat(result.getName(), equalTo("update"));
    }

    @Test
    void updateProductwhichBelongsToAdminByAdmin(){
        products = TestUtils.createProducts(admin, user);
        Product source = createProductSourceToUpdate(admin);
        String guid = products.get(0).getGuid();

        service.update(guid, source, admin);

        Product result = products.stream()
                .filter(product -> product.getGuid().equals(guid))
                .findFirst().get();

        assertThat(products, hasSize(4));
        assertSame(result.getUser(), admin);
        assertThat(result.getKcal(), equalTo(1000.0));
        assertThat(result.getName(), equalTo("update"));
    }

    @Test
    void updateNullProduct(){
        Product source = createProductSourceToUpdate(user);
        products = TestUtils.createProducts(admin, user);
        assertThrows(RestApiException.class, () -> service.update("products.get(0).getGuid()", source, user));
        assertThrows(RestApiException.class, () -> service.update("products.get(0).getGuid()", source, admin));

        assertThrows(RestApiException.class, () -> service.update(null, source, user));
        assertThrows(RestApiException.class, () -> service.update(null, source, admin));

        assertThrows(RestApiException.class, () -> service.update(products.get(0).getGuid(), null, user));
        assertThrows(RestApiException.class, () -> service.update(products.get(0).getGuid(), null, admin));

        assertThrows(RestApiException.class, () -> service.update(products.get(0).getGuid(), source, null));

    }
}
