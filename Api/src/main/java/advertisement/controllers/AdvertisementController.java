package advertisement.controllers;

import advertisement.AdvertisementFilter;
import advertisement.DTOs.request.AdvertisementRequestDTO;
import advertisement.DTOs.response.AdvertisementResponseDTO;
import advertisement.mappers.IAdvertisementDTOToModelMapper;
import advertisement.models.Advertisement;
import advertisement.services.interfaces.IAdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/advertisement")
public class AdvertisementController {
    @Autowired
    private IAdvertisementService advertisementService;
    @Autowired
    private IAdvertisementDTOToModelMapper advertisementDTOToModelMapper;

    @GetMapping
    public ResponseEntity<List<AdvertisementResponseDTO>> getAdvertisements(
            @ModelAttribute AdvertisementFilter filter
    ) {
        List<AdvertisementResponseDTO> response = advertisementDTOToModelMapper
                .toDTOList(advertisementService.
                        getAdvertisements(filter)
                );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{user_login}")
    public ResponseEntity<List<AdvertisementResponseDTO>> getSalesHistory (@PathVariable("user_login") String login) {
        List<Advertisement> advertisements = advertisementService.getSalesHistory(login);
        List<AdvertisementResponseDTO> response = advertisementDTOToModelMapper
                .toDTOList(advertisementService.
                        getSalesHistory(login)
                );
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementResponseDTO> addAdvertisement(
            @RequestPart("data") AdvertisementRequestDTO advertisementRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile multipartFile
    ) throws IOException {
        AdvertisementResponseDTO response = advertisementDTOToModelMapper.toDTO(
                advertisementService.addAdvertisement(
                        advertisementDTOToModelMapper.toModel(advertisementRequestDTO),
                        multipartFile
                )
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementResponseDTO> editAdvertisement(
            @RequestPart("data") AdvertisementRequestDTO advertisementRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile multipartFile
    ) throws IOException, IllegalAccessException {
        AdvertisementResponseDTO response = advertisementDTOToModelMapper.toDTO(
                advertisementService.editAdvertisement(
                        advertisementDTOToModelMapper.toModel(advertisementRequestDTO),
                        multipartFile
                )
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/prepay")
    public ResponseEntity<AdvertisementResponseDTO> prepayAdvertisement(
            @RequestBody AdvertisementRequestDTO advertisementRequestDTO
    ) {
        AdvertisementResponseDTO response = advertisementDTOToModelMapper.toDTO(
                advertisementService.prepayAdvertisement(
                        advertisementDTOToModelMapper.toModel(advertisementRequestDTO)
                )
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/close")
    public ResponseEntity<AdvertisementResponseDTO> closeAdvertisement(
            @RequestBody AdvertisementRequestDTO advertisementRequestDTO
    ) throws IllegalAccessException {
        AdvertisementResponseDTO response = advertisementDTOToModelMapper.toDTO(
                advertisementService.closeAdvertisement(
                        advertisementDTOToModelMapper.toModel(advertisementRequestDTO)
                )
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<AdvertisementResponseDTO> deleteAdvertisement(
            @RequestBody AdvertisementRequestDTO advertisementRequestDTO
    ) throws IOException {
        AdvertisementResponseDTO response = advertisementDTOToModelMapper.toDTO(
                advertisementService.deleteAdvertisement(
                        advertisementDTOToModelMapper.toModel(advertisementRequestDTO)
                )
        );
        return ResponseEntity.ok(response);
    }
}
