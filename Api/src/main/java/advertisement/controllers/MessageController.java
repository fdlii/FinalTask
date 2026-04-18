package advertisement.controllers;

import advertisement.DTOs.request.MessageRequestDTO;
import advertisement.DTOs.response.MessageResponseDTO;
import advertisement.mappers.IMessageDTOToModelMapper;
import advertisement.services.interfaces.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private IMessageService messageService;
    @Autowired
    private IMessageDTOToModelMapper messageDTOToModelMapper;

    @GetMapping("/{sender}/{reciever}")
    public ResponseEntity<List<MessageResponseDTO>> getChatMessages(
            @PathVariable("sender") String senderLogin,
            @PathVariable("reciever") String recieverLogin
    ){
        List<MessageResponseDTO> response = messageDTOToModelMapper
                .toDTOList(messageService
                        .getChatMessages(senderLogin, recieverLogin));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MessageResponseDTO> sendMessage(@RequestBody MessageRequestDTO messageRequestDTO) {
        MessageResponseDTO response = messageDTOToModelMapper
                .toDTO(messageService
                        .sendMessage(messageDTOToModelMapper
                                .toModel(messageRequestDTO)));
        return ResponseEntity.ok(response);
    }
}
