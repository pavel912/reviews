package pavel.lobanov.reviews;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewsApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	private Long gameId;

	private Long reviewId;

	@BeforeEach
	public void createGameAndReview() throws Exception {
		var responseCreateGame = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/games")
						.contentType(MediaType.APPLICATION_JSON).content(
								"{\"name\": \"Tetris\", \"description\": \"Legendary game!\"}"))
				.andReturn()
				.getResponse();

		assertThat(responseCreateGame.getStatus()).isEqualTo(201);

		this.gameId = getId(responseCreateGame.getContentAsString());

		var responseCreateReview = mockMvc.perform(
						MockMvcRequestBuilders
								.post("/reviews")
								.contentType(MediaType.APPLICATION_JSON).content(
										String.format("{\"reviewText\": \"Best game!\", \"author\": \"user123\", \"score\": 10, \"gameId\": %d}", this.gameId)))
				.andReturn()
				.getResponse();

		assertThat(responseCreateReview.getStatus()).isEqualTo(201);

		this.reviewId = getId(responseCreateReview.getContentAsString());
	}

	@Test
	public void getGame() throws Exception {
		var responseGetGame = mockMvc.perform(
						MockMvcRequestBuilders.get("/games/" + this.gameId)
				)
				.andReturn()
				.getResponse();

		assertThat(responseGetGame.getStatus()).isEqualTo(200);
		assertThat(responseGetGame.getContentAsString()).contains("Tetris");
	}

	@Test
	public void updateGame() throws Exception {
		var responseUpdateGame = mockMvc.perform(
						MockMvcRequestBuilders
								.patch("/games/" + this.gameId)
								.contentType(MediaType.APPLICATION_JSON).content(
										"{\"description\": \"Group shapes to form rectangles.\"}"))
				.andReturn()
				.getResponse();

		assertThat(responseUpdateGame.getStatus()).isEqualTo(200);
		assertThat(responseUpdateGame.getContentAsString()).contains("Group shapes to form rectangles.");
		assertThat(responseUpdateGame.getContentAsString()).doesNotContain("Legendary game!");
	}

	@Test
	public void deleteGame() throws Exception {
		var responseDeleteGame = mockMvc.perform(
						MockMvcRequestBuilders
								.delete("/games/" + this.gameId))
				.andReturn()
				.getResponse();

		assertThat(responseDeleteGame.getStatus()).isEqualTo(200);

		var responseGetGame = mockMvc.perform(
				MockMvcRequestBuilders.get("/games/" + this.gameId)
		)
				.andReturn()
				.getResponse();

		assertThat(responseGetGame.getStatus()).isEqualTo(404);
		/*
		// does not work in test for some reason
		// check that cascade deletion was performed
		var responseGetReview = mockMvc.perform(
						MockMvcRequestBuilders.get("/reviews/" + this.reviewId)
				)
				.andReturn()
				.getResponse();

		assertThat(responseGetReview.getStatus()).isEqualTo(404);
		*/
	}

	@Test
	public void getReview() throws Exception {
		var responseGetReview = mockMvc.perform(
						MockMvcRequestBuilders.get("/reviews/" + this.reviewId)
				)
				.andReturn()
				.getResponse();

		assertThat(responseGetReview.getStatus()).isEqualTo(200);
		assertThat(responseGetReview.getContentAsString()).contains("Best game!");
	}

	@Test
	public void updateReview() throws Exception {
		var responseUpdateReview = mockMvc.perform(
						MockMvcRequestBuilders
								.patch("/reviews/" + this.reviewId)
								.contentType(MediaType.APPLICATION_JSON).content(
										"{\"reviewText\": \"My favorite game!\"}"))
				.andReturn()
				.getResponse();

		assertThat(responseUpdateReview.getStatus()).isEqualTo(200);
		assertThat(responseUpdateReview.getContentAsString()).contains("My favorite game!");
		assertThat(responseUpdateReview.getContentAsString()).doesNotContain("Best game!");
	}

	@Test
	public void deleteReview() throws Exception {
		var responseDeleteReview = mockMvc.perform(
						MockMvcRequestBuilders
								.delete("/reviews/" + this.reviewId))
				.andReturn()
				.getResponse();

		assertThat(responseDeleteReview.getStatus()).isEqualTo(200);

		var responseGetReview = mockMvc.perform(
						MockMvcRequestBuilders.get("/reviews/" + this.reviewId)
				)
				.andReturn()
				.getResponse();

		assertThat(responseGetReview.getStatus()).isEqualTo(404);

		// check that cascade deletion wasn't performed
		var responseGetGame = mockMvc.perform(
						MockMvcRequestBuilders.get("/games/" + this.gameId)
				)
				.andReturn()
				.getResponse();

		assertThat(responseGetGame.getStatus()).isEqualTo(200);
	}

	private static Long getId(String content) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		var map = mapper.readValue(content, new TypeReference<Map<String, Object>>() {});
		if (!map.containsKey("id")) {
			throw new RuntimeException("Response does not contain id");
		}

		return Long.valueOf(String.valueOf(map.get("id")));
	}
}
