package kg.course.library.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.course.library.Library;
import kg.course.library.Reader;
import kg.course.library.ReaderCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Читатели", description = "Операции с читателями")
@RestController
@RequestMapping("/api/readers")
@Validated
public class ReaderController {

    private final Library library;
    private final Clock clock;

    public ReaderController(Library library, Clock clock) {
        this.library = library;
        this.clock = clock;
    }

    @Operation(summary = "Получить список всех читателей")
    @GetMapping
    public ResponseEntity<List<ReaderResponse>> getAllReaders() {
        List<ReaderResponse> responses = library.findAllReaders().stream()
                .map(ReaderResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Получить читателя по номеру билета")
    @GetMapping("/{cardNumber}")
    public ResponseEntity<ReaderResponse> getReader(@PathVariable String cardNumber) {
        Reader reader = library.getReader(cardNumber);
        return ResponseEntity.ok(ReaderResponse.from(reader));
    }

    @Operation(summary = "Зарегистрировать нового читателя")
    @PostMapping
    public ResponseEntity<Void> registerReader(@Validated @RequestBody CreateReaderRequest request) {
        ReaderCategory category = ReaderCategory.valueOf(request.category().toUpperCase());
        Reader reader = new Reader(request.cardNumber(), request.fullName(), category);
        library.registerReader(reader);

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{cardNumber}")
                        .buildAndExpand(request.cardNumber())
                        .toUri()
        ).build();
    }

    @Operation(summary = "Получить активные выдачи читателя")
    @GetMapping("/{cardNumber}/loans")
    public ResponseEntity<List<LoanResponse>> getReaderLoans(@PathVariable String cardNumber) {
        library.getReader(cardNumber); // проверяем что читатель существует

        LocalDate today = LocalDate.now(clock);
        List<LoanResponse> responses = library.activeLoans(cardNumber).stream()
                .map(loan -> new LoanResponse(
                        loan.id(),
                        loan.isbn().value(),
                        loan.cardNumber(),
                        loan.issuedOn(),
                        loan.dueOn(),
                        loan.returnedOn(),
                        loan.isOverdue(today)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}