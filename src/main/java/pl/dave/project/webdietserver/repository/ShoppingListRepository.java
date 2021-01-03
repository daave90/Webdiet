package pl.dave.project.webdietserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.dave.project.webdietserver.entity.ShoppingList;

@Repository
public interface ShoppingListRepository extends MongoRepository<ShoppingList, String> {
}
