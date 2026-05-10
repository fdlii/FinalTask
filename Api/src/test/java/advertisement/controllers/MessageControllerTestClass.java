package advertisement.controllers;

import advertisement.DTOs.request.MessageRequestDTO;
import advertisement.DTOs.response.MessageResponseDTO;
import advertisement.JwtHandler;
import advertisement.mappers.IMessageDTOToModelMapper;
import advertisement.models.Message;
import advertisement.security.LoginPasswordUserDetailsService;
import advertisement.security.exception.JwtAuthenticationEntryPoint;
import advertisement.services.interfaces.IMessageService;
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

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MessageControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IMessageService messageService;

    @MockitoBean
    private IMessageDTOToModelMapper messageDTOToModelMapper;

    @MockitoBean
    private JwtHandler jwtHandler;

    @MockitoBean
    private LoginPasswordUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint entryPoint;

    private MessageRequestDTO validMessageRequestDTO;
    private MessageResponseDTO messageResponseDTO;
    private Message message;

    @BeforeEach
    void setUp() {
        validMessageRequestDTO = MessageRequestDTO.builder()
                .senderLogin("sender@mail.com")
                .recieverLogin("receiver@mail.com")
                .content("Hello!")
                .build();

        message = Message.builder()
                .sentAt(LocalDateTime.now())
                .content("Hello!")
                .build();

        messageResponseDTO = MessageResponseDTO.builder()
                .senderLogin("sender@mail.com")
                .recieverLogin("receiver@mail.com")
                .sentAt(LocalDateTime.now())
                .content("Hello!")
                .build();
    }

    // ---------- getChatMessages ----------

    @Test
    void getChatMessagesSuccessfully() throws Exception {
        //Given

        //When
        when(messageService.getChatMessages("sender@mail.com", "receiver@mail.com"))
                .thenReturn(List.of(message));
        when(messageDTOToModelMapper.toDTOList(anyList()))
                .thenReturn(List.of(messageResponseDTO));

        //Then
        mockMvc.perform(get("/message/{sender}/{reciever}",
                        "sender@mail.com", "receiver@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].senderLogin").value("sender@mail.com"))
                .andExpect(jsonPath("$[0].content").value("Hello!"));

        verify(messageService).getChatMessages("sender@mail.com", "receiver@mail.com");
    }

    @Test
    void getChatMessagesInvalidEmailFormat() throws Exception {
        //Given

        //When

        //Then
        mockMvc.perform(get("/message/{sender}/{reciever}",
                        "not-an-email", "receiver@mail.com"))
                .andExpect(status().isBadRequest());
    }

    // ---------- sendMessage ----------

    @Test
    void sendMessageSuccessfully() throws Exception {
        //Given

        //When
        when(messageDTOToModelMapper.toModel(any(MessageRequestDTO.class)))
                .thenReturn(message);
        when(messageService.sendMessage(any(Message.class)))
                .thenReturn(message);
        when(messageDTOToModelMapper.toDTO(any(Message.class)))
                .thenReturn(messageResponseDTO);

        //Then
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(validMessageRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderLogin").value("sender@mail.com"))
                .andExpect(jsonPath("$.content").value("Hello!"));

        verify(messageService).sendMessage(any(Message.class));
    }

    @Test
    void sendMessageBlankContent() throws Exception {
        //Given
        validMessageRequestDTO.setContent("");

        //When

        //Then
        mockMvc.perform(post("/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(validMessageRequestDTO)))
                .andExpect(status().isBadRequest());
    }
}