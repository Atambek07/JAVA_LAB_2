package kg.course.library.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.course.library.Book;
import kg.course.library.ConflictException;
import kg.course.library.Isbn;
import kg.course.library.Library;
import kg.course.library.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Книги", description = "Операции с книгами в каталоге")
@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {

    private final Library library;

    public BookController(Library library) {
        this.library = library;
    }

    @Operation(summary = "Получить список всех книг")
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks(
            @Parameter(description = "Поисковый запрос для поиска по названию")
            @RequestParam(required = false) String q) {
        
        List<Book> books;
        if (q != null && !q.isBlank()) {
            books = library.findAllBooks().stream()
                    .filter(book -> book.title().toLowerCase().contains(q.toLowerCase()))
                    .toList();
        } else {
            books = library.findAllBooks();
        }
        
        List<BookResponse> responses = books.stream()
                .map(book -> BookResponse.from(book, library))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Получить книгу по ISBN")
    @GetMapping("/{isbn}")
    public ResponseEntity<BookResponse> getBook(@PathVariable String isbn) {
        Book book = library.findBook(new Isbn(isbn))
                .orElseThrow(() -> new NotFoundException("Книга не найдена"));
        
        return ResponseEntity.ok(BookResponse.from(book, library));
    }

    @Operation(summary = "Добавить книгу в каталог")
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Validated @RequestBody CreateBookRequest request) {
        Isbn isbn = new Isbn(request.isbn());
        
        if (library.findBook(isbn).isPresent()) {
            throw new ConflictException("Книга с ISBN " + request.isbn() + " уже существует");
        }
        
        Book book = new Book(isbn, request.title(), request.authors(), request.year());
        library.addBook(book, request.copies());
        
        return ResponseEntity.created(
                org.springframework.web.servlet.support.ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{isbn}")
                        .buildAndExpand(book.isbn().value())
                        .toUri()
        ).body(BookResponse.from(book, library));
    }

    @Operation(summary = "Удалить книгу из каталога")
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        library.removeBook(new Isbn(isbn));
        return ResponseEntity.noContent().build();
    }
}