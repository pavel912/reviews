package pavel.lobanov.reviews.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pavel.lobanov.reviews.domain.Review;
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
    public ReviewDto createReview(@RequestBody @Valid ReviewDto reviewDto) {
        Review review = reviewService.createReview(reviewDto);

        return reviewService.reviewToReviewDto(review);
    }

    @PatchMapping("/{id}")
    public ReviewDto updateReview(@PathVariable long id, @RequestBody @Valid ReviewDto reviewDto) {
        Review review = reviewService.updateReview(id, reviewDto);

        return reviewService.reviewToReviewDto(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        var review = reviewRepository.findById(id);

        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review with id " + id + " not found");
        }

        reviewRepository.delete(review.get());
    }
}

