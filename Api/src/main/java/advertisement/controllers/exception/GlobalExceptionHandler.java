package advertisement.controllers.exception;

import advertisement.exceptions.invalid.MessageInvalidException;
import advertisement.exceptions.invalid.RatingInvalidException;
import advertisement.exceptions.notfound.AdvertisementNotFoundException;
import advertisement.exceptions.notfound.CategoryNotFoundException;
import advertisement.exceptions.notfound.RoleNotFoundException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.exceptions.other.AdvertisementIllegalEditException;
import advertisement.exceptions.other.CategoryAlreadyExistException;
import advertisement.exceptions.other.UserAlreadyExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> {
                    fieldErrors.put(err.getField(), err.getDefaultMessage());
                });

        logger.error("Запрос содержит невалидные данные: {}", fieldErrors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, fieldErrors);
    }

    @ExceptionHandler(MessageInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMessageInvalidException(MessageInvalidException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(RatingInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRatingInvalidException(RatingInvalidException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(AdvertisementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleAdvertisementNotFoundException(AdvertisementNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleRoleNotFoundException(RoleNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleUserNotFoundException(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(AdvertisementIllegalEditException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAdvertisementIllegalEditException(AdvertisementIllegalEditException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(CategoryAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleCategoryAlreadyExistException(CategoryAlreadyExistException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleUserAlreadyExistException(UserAlreadyExistException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleIllegalAccessException(IllegalAccessException ex) {
        return ex.getMessage();
    }

    private Map<String, Object> buildErrorResponse(HttpStatus status, Object details) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", status.value());
        response.put("details", details);
        return response;
    }
}