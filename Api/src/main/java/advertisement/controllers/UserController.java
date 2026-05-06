package advertisement.controllers;

import advertisement.DTOs.request.LoginPasswordRequestDTO;
import advertisement.DTOs.request.UserRequestDTO;
import advertisement.DTOs.response.AdvertisementResponseDTO;
import advertisement.DTOs.response.UserResponseDTO;
import advertisement.mappers.IUserDTOToModelMapper;
import advertisement.services.interfaces.IAdvertisementService;
import advertisement.services.interfaces.IUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserDTOToModelMapper userMapper;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> registerUser(
            @Valid @RequestPart("data") UserRequestDTO userRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile avatar
    ) throws IOException, IllegalAccessException {
        logger.info("Регистрация пользователя.");
        UserResponseDTO response = userMapper.toDTO(
                userService.registerUser(
                        userMapper.toUser(userRequestDTO), avatar
                )
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestBody LoginPasswordRequestDTO loginPasswordRequestDTO) {
        logger.info("Смена пароля пользователя.");
        userService.changePassword(loginPasswordRequestDTO.getLogin(), loginPasswordRequestDTO.getPassword());
        return ResponseEntity.ok("Пароль успешно изменён.");
    }

    @PutMapping(value = "/edit_profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> editProfile(
            @Valid @RequestPart("data") UserRequestDTO userRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile avatar
    ) throws IOException {
        logger.info("Редактирование профиля.");
        UserResponseDTO response = userMapper.toDTO(
                userService.editProfile(
                        userMapper.toUser(userRequestDTO), avatar
                )
        );
        return ResponseEntity.ok(response);
    }
}