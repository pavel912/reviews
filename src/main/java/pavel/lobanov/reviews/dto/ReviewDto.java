package pavel.lobanov.reviews.dto;

import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private String reviewText;

    @NotNull
    private String author;

    @NotNull
    private Integer score;

    @NotNull
    private Long gameId;
}
