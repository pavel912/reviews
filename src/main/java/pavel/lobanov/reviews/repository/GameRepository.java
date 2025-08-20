package pavel.lobanov.reviews.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import pavel.lobanov.reviews.domain.Game;
import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Long>, PagingAndSortingRepository<Game, Long> {
}
