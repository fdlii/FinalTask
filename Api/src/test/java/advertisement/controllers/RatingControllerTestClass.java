package advertisement.controllers;

import advertisement.DTOs.request.RatingRequestDTO;
import advertisement.DTOs.response.RatingResponseDTO;
import advertisement.JwtHandler;
import advertisement.mappers.IRatingDTOToModelMapper;
import advertisement.models.Rating;
import advertisement.security.LoginPasswordUserDetailsService;
import advertisement.security.exception.JwtAuthenticationEntryPoint;
import advertisement.services.interfaces.IRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RatingControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IRatingService ratingService;

    @MockitoBean
    private IRatingDTOToModelMapper ratingDTOToModelMapper;

    @MockitoBean
    private JwtHandler jwtHandler;

    @MockitoBean
    private LoginPasswordUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint entryPoint;

    private RatingRequestDTO validRatingRequestDTO;
    private RatingResponseDTO ratingResponseDTO;
    private Rating rating;

    @BeforeEach
    void setUp() {
        validRatingRequestDTO = RatingRequestDTO.builder()
                .sellerLogin("seller@mail.com")
                .reviewerLogin("reviewer@mail.com")
                .score(5)
                .comment("Great seller!")
                .build();

        rating = Rating.builder()
                .score(5)
                .comment("Great seller!")
                .writtenAt(LocalDateTime.now())
                .build();

        ratingResponseDTO = RatingResponseDTO.builder()
                .score(5)
                .comment("Great seller!")
                .writtenAt(LocalDateTime.now())
                .build();
    }

    // ---------- getSellerRatings ----------

    @Test
    void getSellerRatingsSuccessfully() throws Exception {
        //Given

        //When
        when(ratingService.getSellerRatings("seller@mail.com"))
                .thenReturn(List.of(rating));
        when(ratingDTOToModelMapper.toDTOList(anyList()))
                .thenReturn(List.of(ratingResponseDTO));

        //Then
        mockMvc.perform(get("/rating/{login}", "seller@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].score").value(5))
                .andExpect(jsonPath("$[0].comment").value("Great seller!"));

        verify(ratingService).getSellerRatings("seller@mail.com");
    }

    @Test
    void getSellerRatingsInvalidEmailFormat() throws Exception {
        //Given

        //When

        //Then
        mockMvc.perform(get("/rating/{login}", "not-an-email"))
                .andExpect(status().isBadRequest());
    }

    // ---------- addRating ----------

    @Test
    void addRatingSuccessfully() throws Exception {
        //Given

        //When
        when(ratingDTOToModelMapper.toModel(any(RatingRequestDTO.class)))
                .thenReturn(rating);
        when(ratingService.addRating(any(Rating.class)))
                .thenReturn(rating);
        when(ratingDTOToModelMapper.toDTO(any(Rating.class)))
                .thenReturn(ratingResponseDTO);

        //Then
        mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(validRatingRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(5))
                .andExpect(jsonPath("$.comment").value("Great seller!"));

        verify(ratingService).addRating(any(Rating.class));
    }

    @Test
    void addRatingScoreOutOfRange() throws Exception {
        //Given
        validRatingRequestDTO.setScore(10);

        //When

        //Then
        mockMvc.perform(post("/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(validRatingRequestDTO)))
                .andExpect(status().isBadRequest());
    }
}