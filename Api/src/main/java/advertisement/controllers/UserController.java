package advertisement.controllers;

import advertisement.DTOs.request.UserRequestDTO;
import advertisement.DTOs.response.AdvertisementResponseDTO;
import advertisement.DTOs.response.UserResponseDTO;
import advertisement.mappers.IUserDTOToModelMapper;
import advertisement.services.interfaces.IAdvertisementService;
import advertisement.services.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserDTOToModelMapper userMapper;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> registerUser(
            @RequestPart("data") UserRequestDTO userRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile avatar
    ) throws IOException, IllegalAccessException {
        UserResponseDTO response = userMapper.toDTO(
                userService.registerUser(
                        userMapper.toUser(userRequestDTO), avatar
                )
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change_password")
    public ResponseEntity<UserResponseDTO> changePassword(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO response = userMapper.toDTO(userService.changePassword(userMapper.toUser(userRequestDTO)));
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/edit_profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> editProfile(
            @RequestPart("data") UserRequestDTO userRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile avatar
    ) throws IOException {
        UserResponseDTO response = userMapper.toDTO(
                userService.editProfile(
                        userMapper.toUser(userRequestDTO), avatar
                )
        );
        return ResponseEntity.ok(response);
    }
}
