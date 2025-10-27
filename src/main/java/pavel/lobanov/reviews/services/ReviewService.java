package pavel.lobanov.reviews.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pavel.lobanov.reviews.domain.Review;
import pavel.lobanov.reviews.domain.User;
import pavel.lobanov.reviews.dto.ReviewDto;
import pavel.lobanov.reviews.repository.GameRepository;
import pavel.lobanov.reviews.repository.ReviewRepository;
import pavel.lobanov.reviews.repository.UserRepository;

@Service
@Transactional
@AllArgsConstructor
public class ReviewService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public Review createReview(ReviewDto reviewDto, User author) {
        Review review = new Review();

        var game = gameRepository.findById(reviewDto.getGameId());

        if (game.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game with id " + reviewDto.getGameId() + " not found");
        }

        review.setGame(game.get());
        review.setReviewText(reviewDto.getReviewText());
        review.setAuthor(author);
        review.setScore(reviewDto.getScore());

        // var savedReview = reviewRepository.save(review);
        // gameObj.addReview(savedReview);
        // gameRepository.save(gameObj);

        return reviewRepository.save(review);
    }

    public Review updateReview(long id, ReviewDto reviewDto) {
        var review = reviewRepository.findById(id);

        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review with id " + id + " not found");
        }

        if (reviewDto.getReviewText() != null) {
            review.get().setReviewText(reviewDto.getReviewText());
        }

        if (reviewDto.getScore() != null) {
            review.get().setScore(reviewDto.getScore());
        }

        return reviewRepository.save(review.get());
    }

    public ReviewDto reviewToReviewDto(Review review) {
        return new ReviewDto(
                review.getId(),
                review.getCreatedAt(),
                review.getReviewText(),
                review.getScore(),
                review.getGame().getId(),
                authenticationService.userToUserDto(review.getAuthor())
        );
    }
}
