package pavel.lobanov.reviews.dto;

import lombok.*;

import java.time.Instant;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private long id;

    private Instant created_at;

    private String reviewText;

    private Integer score;

    private Long gameId;
}
