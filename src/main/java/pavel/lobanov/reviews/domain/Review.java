package pavel.lobanov.reviews.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @NotNull
    @Size(min = 1, max = 5000)
    @Column(name = "review_text")
    private String reviewText;

    @Min(1)
    @Max(10)
    @Column(name = "score")
    private int score;

    @NotNull
    @JoinColumn(name = "game_id")
    @ManyToOne
    private Game game;
}
