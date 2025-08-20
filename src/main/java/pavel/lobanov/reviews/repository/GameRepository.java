package pavel.lobanov.reviews.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pavel.lobanov.reviews.domain.Game;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
}
