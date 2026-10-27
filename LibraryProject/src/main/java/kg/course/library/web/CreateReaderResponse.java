package kg.course.library.web;

import io.swagger.v3.oas.annotations.media.Schema;
import kg.course.library.ReaderCategory;

public record CreateReaderResponse(
        @Schema(description = "Номер билета читателя", example = "R-12345")
        String cardNumber,
        
        @Schema(description = "ФИО читателя", example = "Иванов Иван Иванович")
        String fullName,
        
        @Schema(description = "Категория читателя")
        ReaderCategory category) {
}