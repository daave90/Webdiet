package pl.dave.project.webdietserver.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dave.project.webdietserver.entity.Product;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {
}
