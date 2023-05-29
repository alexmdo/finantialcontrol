package br.com.alexmdo.finantialcontrol.infra;

import java.time.LocalDateTime;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.alexmdo.finantialcontrol.account.exception.AccountNotArchivedException;
import br.com.alexmdo.finantialcontrol.category.exception.CategoryNotFoundException;
import br.com.alexmdo.finantialcontrol.user.exception.UserAlreadyRegisteredException;
import br.com.alexmdo.finantialcontrol.user.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;

@RestControllerAdvice
@Log4j2
public class CustomErrorAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler({ NotFoundException.class, UserNotFoundException.class, CategoryNotFoundException.class, AccountNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception ex, HttpServletRequest request) {
        log.error("Not found error", ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({ UserAlreadyRegisteredException.class, AccountNotArchivedException.class })
    public ResponseEntity<ErrorResponse> handleBusinessException(Exception ex, HttpServletRequest request) {
        log.error("Precondition error", ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.PRECONDITION_FAILED.value(),
                HttpStatus.PRECONDITION_FAILED.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ValidationErrorResponse>> handleError400(MethodArgumentNotValidException e) {
        log.error("Validation error", e);
        var errors = e.getFieldErrors();
        return ResponseEntity.badRequest().body(errors.stream().map(ValidationErrorResponse::new).toList());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ValidationErrorResponse>> handleError400(ConstraintViolationException e) {
        log.error("Validation error", e);
        var errors = e.getConstraintViolations();
        return ResponseEntity.badRequest().body(errors.stream().map(ValidationErrorResponse::new).toList());
    }

    private record ValidationErrorResponse(String field, String message) {

        public ValidationErrorResponse(ConstraintViolation<?> error) {
            this(error.getPropertyPath().toString(), error.getMessage());
        }

        public ValidationErrorResponse(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }

    }

    private record ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {

        public ErrorResponse(int status, String error, String message, String path) {
            this(LocalDateTime.now(), status, error, message, path);
        }

    }

    // Add more exception handlers as needed for different RuntimeExceptions
}