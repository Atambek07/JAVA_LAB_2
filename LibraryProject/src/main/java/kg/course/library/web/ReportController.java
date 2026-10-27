package kg.course.library.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.course.library.Book;
import kg.course.library.Isbn;
import kg.course.library.Library;
import kg.course.library.LibraryReports;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Collectors;

@Tag(name = "Отчёты", description = "Получение статистических отчётов")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final Library library;
    private final LibraryReports reports;

    public ReportController(Library library) {
        this.library = library;
        this.reports = new LibraryReports(library);
    }

    @Operation(summary = "Получить топ-N самых популярных книг")
    @GetMapping("/top")
    public List<BooksResponse.BookDto> getTopBooks(
            @Parameter(description = "Количество книг в топе", schema = @Schema(defaultValue = "5"))
            @RequestParam(defaultValue = "5") int n) {
        
        if (n < 1) {
            throw new IllegalArgumentException("Параметр n должен быть больше 0");
        }
        
        return reports.topBooks(n).stream()
                .map(bookStat -> library.findBook(new Isbn(
                        library.findAllBooks().stream()
                                .filter(b -> b.title().equals(bookStat.title()))
                                .findFirst()
                                .map(Book::isbn)
                                .orElseThrow()
                                .value()
                ))
                .map(book -> new BooksResponse.BookDto(
                        book.isbn().value(),
                        book.title(),
                        book.authors(),
                        book.year(),
                        library.totalCopies(book.isbn()),
                        (int) bookStat.loans()
                ))
                .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Operation(summary = "Получить количество выдач по категориям читателей")
    @GetMapping("/by-category")
    public Map<String, Long> getLoansByCategory() {
        return reports.loansByCategory().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        Map.Entry::getValue
                ));
    }

    @Operation(summary = "Получить всех авторов в алфавитном порядке")
    @GetMapping("/authors")
    public SortedSet<String> getAllAuthors() {
        return reports.allAuthors();
    }
}