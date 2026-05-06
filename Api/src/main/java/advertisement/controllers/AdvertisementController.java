package advertisement.controllers;

import advertisement.AdvertisementFilter;
import advertisement.DTOs.request.AdvertisementManageRequestDTO;
import advertisement.DTOs.request.AdvertisementRequestDTO;
import advertisement.DTOs.response.AdvertisementResponseDTO;
import advertisement.mappers.IAdvertisementDTOToModelMapper;
import advertisement.models.Advertisement;
import advertisement.services.interfaces.IAdvertisementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/advertisement")
@Validated
public class AdvertisementController {
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementController.class);
    @Autowired
    private IAdvertisementService advertisementService;
    @Autowired
    private IAdvertisementDTOToModelMapper advertisementDTOToModelMapper;

    @GetMapping
    public ResponseEntity<List<AdvertisementResponseDTO>> getAdvertisements(
            @ModelAttribute AdvertisementFilter filter
    ) {
        logger.info("Получение объявлений.");
        List<AdvertisementResponseDTO> response = advertisementDTOToModelMapper
                .toDTOList(advertisementService.
                        getAdvertisements(filter)
                );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{user_login}")
    public ResponseEntity<List<AdvertisementResponseDTO>> getSalesHistory (
            @NotBlank(message = "Логин не может быть пустым.")
            @Email(message = "Некорректный формат логина.")
            @PathVariable("user_login") String login
    ) {
        logger.info("Получение истории продаж.");
        List<Advertisement> advertisements = advertisementService.getSalesHistory(login);
        List<AdvertisementResponseDTO> response = advertisementDTOToModelMapper
                .toDTOList(advertisementService.
                        getSalesHistory(login)
                );
        return ResponseEntity.ok(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementResponseDTO> addAdvertisement(
            @Valid @RequestPart("data") AdvertisementRequestDTO advertisementRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile multipartFile
    ) throws IOException {
        logger.info("Добавление объявления.");
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
            @Valid @RequestPart("data") AdvertisementRequestDTO advertisementRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile multipartFile
    ) throws IOException, IllegalAccessException {
        logger.info("Редактирование объявления.");
        AdvertisementResponseDTO response = advertisementDTOToModelMapper.toDTO(
                advertisementService.editAdvertisement(
                        advertisementDTOToModelMapper.toModel(advertisementRequestDTO),
                        multipartFile
                )
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/prepay/{adNumber}")
    public ResponseEntity<String> prepayAdvertisement(@PathVariable("adNumber") Long adNumber) {
        logger.info("Проплата объявления.");
        advertisementService.prepayAdvertisement(adNumber);
        return ResponseEntity.ok("Объявление успешно проплачено.");
    }

    @PutMapping("/close")
    public ResponseEntity<String> closeAdvertisement(
            @Valid @RequestBody AdvertisementManageRequestDTO advertisementRequestDTO
    ) throws IllegalAccessException {
        logger.info("Закрытие объявления.");
        advertisementService.closeAdvertisement(advertisementRequestDTO.getAdNumber(),
                                                advertisementRequestDTO.getUser().getLogin());
        return ResponseEntity.ok("Объявление успешно закрыто.");
    }

    @DeleteMapping("/{adNumber}")
    public ResponseEntity<String> deleteAdvertisement(@PathVariable("adNumber") Long adNumber) throws IOException {
        logger.info("Удаление объявления.");
        advertisementService.deleteAdvertisement(adNumber);
        return ResponseEntity.ok("Объявление успешно удалено.");
    }
}