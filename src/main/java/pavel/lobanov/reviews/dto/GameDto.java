package pavel.lobanov.reviews.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameDto {
    private long id;

    private Instant createdAt;

    @NotNull
    private String name;

    @NotNull
    private String description;

    private List<ReviewDto> reviews;
}
