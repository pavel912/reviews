package pavel.lobanov.reviews.dto;

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

    private String name;

    private String description;

    private List<ReviewDto> reviews;
}
