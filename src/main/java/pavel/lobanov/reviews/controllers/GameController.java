package pavel.lobanov.reviews.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pavel.lobanov.reviews.domain.Game;
import pavel.lobanov.reviews.domain.User;
import pavel.lobanov.reviews.dto.GameDto;
import pavel.lobanov.reviews.repository.GameRepository;
import pavel.lobanov.reviews.services.GameService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/games")
@AllArgsConstructor
public class GameController {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    private static final String ONLY_OWNER_BY_ID = """
            @gameRepository.findById(#id).get().getCreator().getUsername().equals(authentication.getName())
            """;

    @GetMapping("/{id}")
    public GameDto getGame(@PathVariable long id) {
        var game = gameRepository.findById(id);

        if (game.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + id + " not found");
        }

        return gameService.gameToGameDto(game.get());
    }

    @GetMapping(path = "")
    public List<GameDto> getGames(@RequestParam(value = "pageNumber", required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageSize == null) {
            return this.gamesToDtos(gameRepository.findAll());
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return this.gamesToDtos(gameRepository.findAll(pageable));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public GameDto createGame(@RequestBody @Valid GameDto gameDto, Authentication authentication) {
        Game game = gameService.createGame(gameDto, (User) authentication.getPrincipal());

        return gameService.gameToGameDto(game);
    }

    @PatchMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public GameDto updateGame(@PathVariable long id, @RequestBody @Valid GameDto gameDto, Authentication authentication) {
        Game game = gameService.updateGame(id, gameDto);

        return gameService.gameToGameDto(game);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteGame(@PathVariable long id, Authentication authentication) {
        var game = gameRepository.findById(id);

        if (game.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + id + " not found");
        }

        gameRepository.delete(game.get());
    }

    private List<GameDto> gamesToDtos(Iterable<Game> games) {
        List<GameDto> gameDtos = new ArrayList<>();

        for (Game g : games) {
            gameDtos.add(gameService.gameToGameDto(g));
        }

        return gameDtos;
    }
}
