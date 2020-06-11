package pl.dave.project.webdietserver.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dave.project.webdietserver.entity.ShoppingList;

@Repository
public interface ShoppingListRepository extends CrudRepository<ShoppingList, String> {
}
