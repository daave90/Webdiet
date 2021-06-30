package pl.dave.project.webdietserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.stereotype.Service;
import pl.dave.project.webdietserver.dto.product.ProductRequest;
import pl.dave.project.webdietserver.entity.Product;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.entity.enums.UserRole;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.ProductMapper;
import pl.dave.project.webdietserver.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    public Product save(Product product, User user) {
        log.info("**************************************************************************************************");
        log.info("Saving Product: " + product);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);
        product.setUser(user);
        return productRepository.save(product);
    }

    public List<Product> list(User user) {
        log.info("**************************************************************************************************");
        log.info("List all products");
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);

        if (user.getRole() == UserRole.ADMIN) {
            return IteratorUtils.toList(productRepository.findAll().iterator());
        }

        return IteratorUtils.toList(productRepository.findAll().iterator()).stream()
                .filter(product -> product.getUser().getGuid().equals(user.getGuid()))
                .collect(Collectors.toList());
    }

    public Product getByGuid(String guid, User user) {
        log.info("**************************************************************************************************");
        log.info("Get product by guid: " + guid);
        log.info("Login user: " + user);
        Product product = productRepository.findById(guid).orElseThrow(() -> new RestApiException(ErrorCode.PRODUCT_NOT_EXISTS));

        if (user.getRole() == UserRole.ADMIN ||
                (product.getUser().getGuid().equals(user.getGuid()))) {
            log.info("Product founded: " + product);
            return product;
        }

        throw new RestApiException(ErrorCode.GET_WRONG_RESOURCE);
    }

    public Product update(String guid, Product source, User user) {
        log.info("**************************************************************************************************");
        log.info("Update product: " + guid + " source: " + source);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(source == null, ErrorCode.SOURCE_ENTITY_NOT_EXISTS);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);

        Product productToUpdate = productRepository.findById(guid).orElse(null);

        if (productToUpdate != null && (user.getRole() == UserRole.ADMIN ||
                (user.getGuid().equals(productToUpdate.getUser().getGuid())))) {
            User newUser = productToUpdate.getUser();
            productToUpdate = mapper.update(productToUpdate, source);
            productToUpdate.setUser(newUser);
            return productRepository.save(productToUpdate);
        }

        throw new RestApiException(ErrorCode.UPDATE_WRONG_RESOURCE);
    }

    public void delete(Product product, User user) {
        log.info("**************************************************************************************************");
        log.info("Remove product: " + product);
        log.info("Login user: " + user);
        RestApiException.throwExceptionIfConditionIsTrue(product == null, ErrorCode.PRODUCT_NOT_EXISTS);
        RestApiException.throwExceptionIfConditionIsTrue(user == null, ErrorCode.USER_CANNOT_BE_NULL);

        if (user.getRole() == UserRole.ADMIN ||
                (product.getUser().getGuid().equals(user.getGuid()))) {
            productRepository.delete(product);
        } else {
            throw new RestApiException(ErrorCode.DELETE_WRONG_RESOURCE);
        }
    }

    public List<ProductRequest> mapToRequestList(User user) {
        log.info("**************************************************************************************************");
        log.info("Mapping to product requests");
        log.info("Login user: " + user);
        List<Product> products = list(user);
        return mapper.toRequests(products);
    }
}
