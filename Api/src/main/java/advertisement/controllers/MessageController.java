package advertisement.controllers;

import advertisement.DTOs.request.MessageRequestDTO;
import advertisement.DTOs.response.MessageResponseDTO;
import advertisement.mappers.IMessageDTOToModelMapper;
import advertisement.services.interfaces.IMessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    private IMessageService messageService;
    @Autowired
    private IMessageDTOToModelMapper messageDTOToModelMapper;

    @GetMapping("/{sender}/{reciever}")
    public ResponseEntity<List<MessageResponseDTO>> getChatMessages(
            @NotBlank(message = "Логин не может быть пустым.")
            @Email(message = "Некорректный формат логина.")
            @PathVariable("sender") String senderLogin,

            @NotBlank(message = "Логин не может быть пустым.")
            @Email(message = "Некорректный формат логина.")
            @PathVariable("reciever") String recieverLogin
    ){
        logger.info("Получение сообщений чата.");
        List<MessageResponseDTO> response = messageDTOToModelMapper
                .toDTOList(messageService
                        .getChatMessages(senderLogin, recieverLogin));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(@Valid @RequestBody MessageRequestDTO messageRequestDTO) {
        logger.info("Отправка сообщения.");
        MessageResponseDTO response = messageDTOToModelMapper
                .toDTO(messageService
                        .sendMessage(messageDTOToModelMapper
                                .toModel(messageRequestDTO)));
        return ResponseEntity.ok(response);
    }
}
