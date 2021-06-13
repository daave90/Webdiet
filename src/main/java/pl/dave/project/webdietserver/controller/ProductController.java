package pl.dave.project.webdietserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.dave.project.webdietserver.dto.product.ProductListRecord;
import pl.dave.project.webdietserver.dto.product.ProductRequest;
import pl.dave.project.webdietserver.entity.Product;
import pl.dave.project.webdietserver.entity.User;
import pl.dave.project.webdietserver.exeption.ErrorCode;
import pl.dave.project.webdietserver.exeption.RestApiException;
import pl.dave.project.webdietserver.mapper.ProductMapper;
import pl.dave.project.webdietserver.service.ProductService;
import pl.dave.project.webdietserver.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/products")
public class ProductController {

    private final ProductService service;
    private final ProductMapper mapper;
    private final UserService userService;


    @GetMapping(value = "/{guid}")
    public ProductListRecord getProductByGuid(@PathVariable String guid) {
        return mapper.toListRecord(service.getByGuid(guid, userService.getCurrentLoginUser()));
    }

    @GetMapping
    public List<ProductListRecord> listAllProducts() {
        return mapper.toListRecords(service.list(userService.getCurrentLoginUser()));
    }

    @PostMapping
    public ProductListRecord create(@RequestBody ProductRequest request) {
        Product newProduct = mapper.toEntity(request);
        return mapper.toListRecord(service.save(newProduct, userService.getCurrentLoginUser()));
    }

    @PutMapping("/{guid}")
    public ProductListRecord updateProduct(@PathVariable String guid, @RequestBody ProductRequest request) {
        Product product = service.update(guid, mapper.toEntity(request), userService.getCurrentLoginUser());
        return mapper.toListRecord(product);
    }

    @DeleteMapping("/{guid}")
    public void deleteProduct(@PathVariable String guid) {
        try {
            User loginUser = userService.getCurrentLoginUser();
            service.delete(service.getByGuid(guid, loginUser), loginUser);
        } catch (RestApiException ex) {
            throw new RestApiException(ErrorCode.DELETE_WRONG_RESOURCE);
        }
    }
}
