package pl.dave.project.webdietserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.dave.project.webdietserver.entity.Recipe;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
}
