package pavel.lobanov.reviews.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @NotBlank
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;

    @NotNull
    @Size(min = 1, max = 2000)
    @Column(name = "description")
    private String description;
}
