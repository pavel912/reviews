package pavel.lobanov.reviews.repository;

import org.springframework.data.repository.CrudRepository;
import pavel.lobanov.reviews.domain.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
