package pavel.lobanov.reviews.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;

    private Instant createdAt;

    @NotNull
    private String username;

    @NotNull
    private String password;
}
