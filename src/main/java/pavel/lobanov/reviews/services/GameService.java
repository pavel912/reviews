package pavel.lobanov.reviews.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pavel.lobanov.reviews.domain.Game;
import pavel.lobanov.reviews.dto.GameDto;
import pavel.lobanov.reviews.repository.GameRepository;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ReviewService reviewService;


    public Game createGame(GameDto gameDto) {
        Game game = new Game();
        game.setName(gameDto.getName());
        game.setDescription(gameDto.getDescription());

        return gameRepository.save(game);
    }

    public Game updateGame(long id, GameDto gameDto) {
        Optional<Game> game = gameRepository.findById(id);

        if (game.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + id + " not found");
        }

        if (gameDto.getName() != null) {
            game.get().setName(gameDto.getName());
        }

        if (gameDto.getDescription() != null) {
            game.get().setDescription(gameDto.getDescription());
        }

        return gameRepository.save(game.get());
    }

    public GameDto gameToGameDto(Game game) {
        return new GameDto(
                game.getId(),
                game.getCreatedAt(),
                game.getName(),
                game.getDescription(),
                game.getReviews().stream().map(reviewService::reviewToReviewDto).toList()
        );
    }
}
