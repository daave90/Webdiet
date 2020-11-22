package pl.dave.project.webdietserver.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.dave.project.webdietserver.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User findByEmail(String username);
}
