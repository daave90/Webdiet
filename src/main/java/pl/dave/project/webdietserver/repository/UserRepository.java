package pl.dave.project.webdietserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.dave.project.webdietserver.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}
