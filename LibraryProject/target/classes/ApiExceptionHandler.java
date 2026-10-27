package kg.course.library.web;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import kg.course.library.*;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundException(NotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(404);
        problem.setTitle("Not Found");
        problem.setDetail(ex.getMessage());
        problem.setType(java.net.URI.create("about:blank"));
        problem.setProperty("instance", "/api");
        return ResponseEntity.status(404).body(problem);
    }

    @ExceptionHandler({ConflictException.class, BookUnavailableException.class, 
                       LimitExceededException.class, OverdueException.class, IllegalStateException.class})
    public ResponseEntity<ProblemDetail> handleConflict(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(409);
        problem.setTitle("Conflict");
        problem.setDetail(ex.getMessage());
        problem.setType(java.net.URI.create("about:blank"));
        problem.setProperty("instance", "/api");
        return ResponseEntity.status(409).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        problem.setType(java.net.URI.create("about:blank"));
        problem.setProperty("instance", "/api");
        return ResponseEntity.status(400).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(400);
        problem.setTitle("Bad Request");
        problem.setDetail("Ошибки валидации");
        problem.setType(java.net.URI.create("about:blank"));
        problem.setProperty("instance", "/api");
        
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        problem.setProperty("errors", errors);
        
        return ResponseEntity.status(400).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(500);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Произошла внутренняя ошибка сервера");
        problem.setType(java.net.URI.create("about:blank"));
        problem.setProperty("instance", "/api");
        
        // Log the exception but don't include in response
        ex.printStackTrace();
        
        return ResponseEntity.status(500).body(problem);
    }

    public record ValidationError(String field, String message) {}
}