package kg.course.library.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record IssueRequest(
        @Schema(description = "Номер билета читателя", example = "R-12345")
        @NotBlank(message = "Номер билета не может быть пустым")
        String cardNumber,
        
        @Schema(description = "ISBN книги", example = "978-5-123456-78-9")
        @NotBlank(message = "ISBN не может быть пустым")
        String isbn) {
}