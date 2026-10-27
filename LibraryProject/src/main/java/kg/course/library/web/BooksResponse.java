package kg.course.library.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record BooksResponse(
        @Schema(description = "Список книг")
        List<BookDto> books) {

    public record BookDto(
            @Schema(description = "ISBN книги")
            String isbn,
            @Schema(description = "Название книги")
            String title,
            @Schema(description = "Авторы книги")
            List<String> authors,
            @Schema(description = "Год издания")
            int year,
            @Schema(description = "Общее количество экземпляров")
            int totalCopies,
            @Schema(description = "Доступное количество экземпляров")
            int availableCopies) {
    }
}