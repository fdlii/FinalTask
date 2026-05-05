package advertisement.controllers;

import advertisement.DTOs.request.RatingRequestDTO;
import advertisement.DTOs.response.RatingResponseDTO;
import advertisement.mappers.IRatingDTOToModelMapper;
import advertisement.services.interfaces.IRatingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingController {
    @Autowired
    private IRatingService ratingService;
    @Autowired
    private IRatingDTOToModelMapper ratingDTOToModelMapper;

    @GetMapping("/{login}")
    public ResponseEntity<List<RatingResponseDTO>> getSellerRatings(
            @NotBlank(message = "Логин не может быть пустым.")
            @Email(message = "Некорректный формат логина.")
            @PathVariable("login") String login
    ) {
        List<RatingResponseDTO> response = ratingDTOToModelMapper
                .toDTOList(ratingService
                        .getSellerRatings(login));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<RatingResponseDTO> addRating(@Valid @RequestBody RatingRequestDTO ratingRequestDTO) {
        RatingResponseDTO response = ratingDTOToModelMapper
                .toDTO(ratingService
                        .addRating(ratingDTOToModelMapper
                                .toModel(ratingRequestDTO)));
        return ResponseEntity.ok(response);
    }
}
