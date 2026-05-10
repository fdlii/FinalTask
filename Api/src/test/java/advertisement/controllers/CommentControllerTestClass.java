package advertisement.controllers;

import advertisement.DTOs.request.CommentRequestDTO;
import advertisement.DTOs.response.CommentResponseDTO;
import advertisement.JwtHandler;
import advertisement.mappers.ICommentDTOToModelMapper;
import advertisement.models.Comment;
import advertisement.security.LoginPasswordUserDetailsService;
import advertisement.security.exception.JwtAuthenticationEntryPoint;
import advertisement.services.interfaces.ICommentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICommentService commentService;

    @MockitoBean
    private ICommentDTOToModelMapper commentDTOToModelMapper;

    @MockitoBean
    private JwtHandler jwtHandler;

    @MockitoBean
    private LoginPasswordUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint entryPoint;

    private CommentRequestDTO validCommentRequestDTO;
    private CommentResponseDTO commentResponseDTO;
    private Comment comment;

    @BeforeEach
    void setUp() {
        validCommentRequestDTO = CommentRequestDTO.builder()
                .adNumber(1L)
                .senderLogin("test@mail.com")
                .content("Sample comment")
                .build();

        comment = Comment.builder()
                .sentAt(LocalDateTime.now())
                .content("Sample comment")
                .build();

        commentResponseDTO = CommentResponseDTO.builder()
                .sentAt(LocalDateTime.now())
                .content("Sample comment")
                .build();
    }

    // ---------- leaveComment ----------

    @Test
    void leaveCommentSuccessfully() throws Exception {
        //Given

        //When
        when(commentDTOToModelMapper.toModel(any(CommentRequestDTO.class)))
                .thenReturn(comment);
        when(commentService.addComment(any(Comment.class)))
                .thenReturn(comment);
        when(commentDTOToModelMapper.toDTO(any(Comment.class)))
                .thenReturn(commentResponseDTO);

        //Then
        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(validCommentRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Sample comment"));

        verify(commentService).addComment(any(Comment.class));
    }

    @Test
    void leaveCommentBlankContent() throws Exception {
        //Given
        validCommentRequestDTO.setContent("");

        //When

        //Then
        mockMvc.perform(post("/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(validCommentRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    // ---------- getAdvertisementComments ----------

    @Test
    void getAdvertisementCommentsSuccessfully() throws Exception {
        //Given

        //When
        when(commentService.getAdvertisementComments(1L))
                .thenReturn(List.of(comment));
        when(commentDTOToModelMapper.toDTOList(anyList()))
                .thenReturn(List.of(commentResponseDTO));

        //Then
        mockMvc.perform(get("/comment/{adNumber}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Sample comment"));

        verify(commentService).getAdvertisementComments(1L);
    }
}