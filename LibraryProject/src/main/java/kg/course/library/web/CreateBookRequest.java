package kg.course.library.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateBookRequest(
        @Schema(description = "ISBN книги", example = "978-5-123456-78-9")
        @NotBlank(message = "ISBN не может быть пустым")
        String isbn,
        
        @Schema(description = "Название книги", example = "Прекрасный новый мир")
        @NotBlank(message = "Название не может быть пустым")
        String title,
        
        @Schema(description = "Список авторов")
        @NotEmpty(message = "Список авторов не может быть пустым")
        List<@NotBlank String> authors,
        
        @Schema(description = "Год издания", example = "1990")
        @Min(value = 1450, message = "Год издания должен быть не меньше 1450")
        int year,
        
        @Schema(description = "Количество экземпляров", example = "5")
        @Min(value = 1, message = "Количество экземпляров должно быть не меньше 1")
        int copies) {
}