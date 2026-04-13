package advertisement.controllers;

import advertisement.DTOs.request.CommentRequestDTO;
import advertisement.DTOs.response.CommentResponseDTO;
import advertisement.mappers.ICommentDTOToModelMapper;
import advertisement.services.interfaces.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private ICommentService commentService;
    @Autowired
    private ICommentDTOToModelMapper commentDTOToModelMapper;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> leaveComment(@RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO response = commentDTOToModelMapper
                .toDTO(commentService
                    .addComment(commentDTOToModelMapper
                        .toModel(commentRequestDTO)));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{adNumber}")
    public ResponseEntity<List<CommentResponseDTO>> getAdvertisementComments(@PathVariable("adNumber") Long adNumber) {
        List<CommentResponseDTO> response = commentDTOToModelMapper
                .toDTOList(commentService
                        .getAdvertisementComments(adNumber));
        return ResponseEntity.ok(response);
    }
}
