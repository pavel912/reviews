package pavel.lobanov.reviews.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pavel.lobanov.reviews.domain.Game;
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

    @GetMapping("/{id}")
    public GameDto getGame(@PathVariable long id) {
        var game = gameRepository.findById(id);

        if (game.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + id + " not found");
        }

        return gameService.gameToGameDto(game.get());
    }

    @GetMapping(path = "")
    public List<GameDto> getGames() {
        List<GameDto> gameDtos = new ArrayList<>();
        var games = gameRepository.findAll();

        for (Game g : games) {
            gameDtos.add(gameService.gameToGameDto(g));
        }

        return gameDtos;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public GameDto createGame(@RequestBody @Valid GameDto gameDto) {
        Game game = gameService.createGame(gameDto);

        return gameService.gameToGameDto(game);
    }

    @PatchMapping("/{id}")
    public GameDto updateGame(@PathVariable long id, @RequestBody @Valid GameDto gameDto) {
        Game game = gameService.updateGame(id, gameDto);

        return gameService.gameToGameDto(game);
    }

    @DeleteMapping("/{id}")
    public void deleteGame(@PathVariable long id) {
        var game = gameRepository.findById(id);

        if (game.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + id + " not found");
        }

        gameRepository.delete(game.get());
    }
}
