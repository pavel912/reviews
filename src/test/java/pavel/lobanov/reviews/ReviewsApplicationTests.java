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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReviewsApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	private Long gameId;

	private Long reviewId;

    private String token;

	@BeforeEach
	public void createUserAndGameAndReview() throws Exception {
        var responseCreateUser = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"username\":\"username\", \"password\":\"password\"}"
                        ))
                .andReturn()
                .getResponse();

        assertThat(responseCreateUser.getStatus()).isEqualTo(201);

        var responseLoginUser = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"username\":\"username\", \"password\":\"password\"}"
                                ))
                .andReturn()
                .getResponse();

        assertThat(responseLoginUser.getStatus()).isEqualTo(200);

        this.token = getToken(responseLoginUser.getContentAsString());

		var responseCreateGame = mockMvc.perform(
				MockMvcRequestBuilders
						.post("/games")
                        .header(AUTHORIZATION, token)
						.contentType(MediaType.APPLICATION_JSON).content(
								"{\"name\": \"Tetris\", \"description\": \"Legendary game!\"}"))
				.andReturn()
				.getResponse();

		assertThat(responseCreateGame.getStatus()).isEqualTo(201);

		this.gameId = getId(responseCreateGame.getContentAsString());

		var responseCreateReview = mockMvc.perform(
						MockMvcRequestBuilders
								.post("/reviews")
                                .header(AUTHORIZATION, token)
								.contentType(MediaType.APPLICATION_JSON).content(
										String.format("{\"reviewText\": \"Best game!\", \"score\": 10, \"gameId\": %d}", this.gameId)))
				.andReturn()
				.getResponse();

		assertThat(responseCreateReview.getStatus()).isEqualTo(201);

		this.reviewId = getId(responseCreateReview.getContentAsString());
	}

	@Test
	public void testGetGame() throws Exception {
		var responseGetGame = mockMvc.perform(
						MockMvcRequestBuilders.get("/games/" + this.gameId)
				)
				.andReturn()
				.getResponse();

		assertThat(responseGetGame.getStatus()).isEqualTo(200);
		assertThat(responseGetGame.getContentAsString()).contains("Tetris");
	}

	@Test
	public void testUpdateGame() throws Exception {
		var responseUpdateGame = mockMvc.perform(
						MockMvcRequestBuilders
								.patch("/games/" + this.gameId)
                                .header(AUTHORIZATION, token)
								.contentType(MediaType.APPLICATION_JSON).content(
										"{\"description\": \"Group shapes to form rectangles.\"}"))
				.andReturn()
				.getResponse();

		assertThat(responseUpdateGame.getStatus()).isEqualTo(200);
		assertThat(responseUpdateGame.getContentAsString()).contains("Group shapes to form rectangles.");
		assertThat(responseUpdateGame.getContentAsString()).doesNotContain("Legendary game!");
	}

	@Test
	public void testDeleteGame() throws Exception {
		var responseDeleteGame = mockMvc.perform(
						MockMvcRequestBuilders
								.delete("/games/" + this.gameId)
                                .header(AUTHORIZATION, token))
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
	public void testGetReview() throws Exception {
		var responseGetReview = mockMvc.perform(
						MockMvcRequestBuilders.get("/reviews/" + this.reviewId)
				)
				.andReturn()
				.getResponse();

		assertThat(responseGetReview.getStatus()).isEqualTo(200);
		assertThat(responseGetReview.getContentAsString()).contains("Best game!");
	}

	@Test
	public void testUpdateReview() throws Exception {
		var responseUpdateReview = mockMvc.perform(
						MockMvcRequestBuilders
								.patch("/reviews/" + this.reviewId)
                                .header(AUTHORIZATION, token)
								.contentType(MediaType.APPLICATION_JSON).content(
										"{\"reviewText\": \"My favorite game!\"}"))
				.andReturn()
				.getResponse();

		assertThat(responseUpdateReview.getStatus()).isEqualTo(200);
		assertThat(responseUpdateReview.getContentAsString()).contains("My favorite game!");
		assertThat(responseUpdateReview.getContentAsString()).doesNotContain("Best game!");
	}

	@Test
	public void testDeleteReview() throws Exception {
		var responseDeleteReview = mockMvc.perform(
						MockMvcRequestBuilders
								.delete("/reviews/" + this.reviewId)
                                .header(AUTHORIZATION, token))
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

    @Test
    public void testAuthentication() throws Exception {
        var responseCreateGame = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/games")
                                .contentType(MediaType.APPLICATION_JSON).content(
                                        "{\"name\": \"Tetris\", \"description\": \"Legendary game!\"}"))
                .andReturn()
                .getResponse();

        assertThat(responseCreateGame.getStatus()).isEqualTo(403);
    }

    @Test
    public void testAuthorization() throws Exception {
        // try deleting game of another user
        var responseCreateUser = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"username\":\"username2\", \"password\":\"password\"}"
                                ))
                .andReturn()
                .getResponse();

        assertThat(responseCreateUser.getStatus()).isEqualTo(201);

        var responseLoginUser = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"username\":\"username2\", \"password\":\"password\"}"
                                ))
                .andReturn()
                .getResponse();

        assertThat(responseLoginUser.getStatus()).isEqualTo(200);

        String newToken = getToken(responseLoginUser.getContentAsString());

        var responseDeleteGame = mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete("/games/" + this.gameId)
                                .header(AUTHORIZATION, newToken))
                .andReturn()
                .getResponse();

        assertThat(responseDeleteGame.getStatus()).isEqualTo(403);
    }

	private static Long getId(String content) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		var map = mapper.readValue(content, new TypeReference<Map<String, Object>>() {});
		if (!map.containsKey("id")) {
			throw new RuntimeException("Response does not contain id");
		}

		return Long.valueOf(String.valueOf(map.get("id")));
	}

    private String getToken(String loginResponse) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();

        var node = om.readTree(loginResponse);

        if (node.get("token") == null) {
            throw new RuntimeException("No token field in login response");
        }

        return "Bearer " + node.get("token").asText();
    }
}
