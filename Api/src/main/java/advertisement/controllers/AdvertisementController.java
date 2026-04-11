package advertisement.controllers;

import advertisement.DTOs.request.AdvertisementRequestDTO;
import advertisement.DTOs.response.AdvertisementResponseDTO;
import advertisement.mappers.IAdvertisementDTOToModelMapper;
import advertisement.services.interfaces.IAdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/advertisement")
public class AdvertisementController {
    @Autowired
    IAdvertisementService advertisementService;
    @Autowired
    IAdvertisementDTOToModelMapper advertisementDTOToModelMapper;

    @PostMapping
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
}
