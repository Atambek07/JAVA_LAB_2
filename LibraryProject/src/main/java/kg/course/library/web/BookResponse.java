package kg.course.library.web;

import io.swagger.v3.oas.annotations.media.Schema;
import kg.course.library.Book;
import kg.course.library.Library;

public record BookResponse(
        @Schema(description = "ISBN книги")
        String isbn,
        
        @Schema(description = "Название книги")
        String title,
        
        @Schema(description = "Авторы книги")
        java.util.List<String> authors,
        
        @Schema(description = "Год издания")
        int year,
        
        @Schema(description = "Общее количество экземпляров")
        int totalCopies,
        
        @Schema(description = "Доступное количество экземпляров")
        int availableCopies) {
    
    public static BookResponse from(Book book, Library library) {
        return new BookResponse(
                book.isbn().value(),
                book.title(),
                book.authors(),
                book.year(),
                library.totalCopies(book.isbn()),
                library.availableCopies(book.isbn())
        );
    }
}