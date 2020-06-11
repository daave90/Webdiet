package pl.dave.project.webdietserver.util;

import pl.dave.project.webdietserver.entity.Product;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static User createUser(String username, String firstname, String lastName, String password, UserRole role) {
        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(password);
        admin.setEnabled(true);
        admin.setRole(role);
        admin.setLastName(lastName);
        admin.setFirstName(firstname);
        return admin;
    }

    public static List<Product> createProducts(User admin, User user) {
        List<Product> createdProducts = new ArrayList<>();
        Product product1 = new Product();
        product1.setName("Kalafior");
        product1.setKcal(100);
        product1.setUser(admin);

        Product product2 = new Product();
        product2.setName("Ogórki");
        product2.setKcal(200);
        product2.setUser(admin);

        Product product3 = new Product();
        product3.setName("Pomidory");
        product3.setKcal(50);
        product3.setUser(user);

        Product product4 = new Product();
        product4.setName("Brokuły");
        product4.setKcal(150);
        product4.setUser(user);

        createdProducts.add(product1);
        createdProducts.add(product2);
        createdProducts.add(product3);
        createdProducts.add(product4);

        return createdProducts;
    }
}
