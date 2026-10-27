package kg.course.library.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateReaderRequest(
        @Schema(description = "Номер билета читателя", pattern = "R-\\\\d{5}", example = "R-12345")
        @NotBlank(message = "Номер билета не может быть пустым")
        @Pattern(regexp = "R-\\\\d{5}", message = "Номер билета должен быть в формате R-XXXXX")
        String cardNumber,
        
        @Schema(description = "ФИО читателя", example = "Иванов Иван Иванович")
        @NotBlank(message = "ФИО не может быть пустым")
        String fullName,
        
        @Schema(description = "Категория читателя", example = "STUDENT")
        @NotNull(message = "Категория не может быть пустой")
        String category) {
}