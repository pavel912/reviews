package pavel.lobanov.reviews.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pavel.lobanov.reviews.domain.Review;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {

    List<Review> findByGameId(Long gameId);
}
