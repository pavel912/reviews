package pavel.lobanov.reviews.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pavel.lobanov.reviews.domain.Review;
import pavel.lobanov.reviews.domain.User;
import pavel.lobanov.reviews.dto.ReviewDto;
import pavel.lobanov.reviews.repository.ReviewRepository;
import pavel.lobanov.reviews.services.ReviewService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewService reviewService;

    private static final String ONLY_OWNER_BY_ID = """
            @reviewRepository.findById(#id).get().getAuthor().getUsername().equals(authentication.getName())
            """;

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable long id) {
        var review = reviewRepository.findById(id);

        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review with id " + id + " not found");
        }

        return reviewService.reviewToReviewDto(review.get());
    }

    @GetMapping("")
    public List<ReviewDto> getReviews() {
        List<ReviewDto> reviewDtos = new ArrayList<>();
        var reviews = reviewRepository.findAll();

        for (Review r : reviews) {
            reviewDtos.add(reviewService.reviewToReviewDto(r));
        }

        return reviewDtos;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(@RequestBody @Valid ReviewDto reviewDto, Authentication authentication) {
        Review review = reviewService.createReview(reviewDto, (User) authentication.getPrincipal());

        return reviewService.reviewToReviewDto(review);
    }

    @PatchMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public ReviewDto updateReview(@PathVariable long id, @RequestBody @Valid ReviewDto reviewDto, Authentication authentication) {
        Review review = reviewService.updateReview(id, reviewDto);

        return reviewService.reviewToReviewDto(review);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteReview(@PathVariable long id, Authentication authentication) {
        var review = reviewRepository.findById(id);

        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review with id " + id + " not found");
        }

        reviewRepository.delete(review.get());
    }
}

