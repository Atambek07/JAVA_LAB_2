package kg.course.library.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.course.library.BookUnavailableException;
import kg.course.library.ConflictException;
import kg.course.library.Library;
import kg.course.library.LimitExceededException;
import kg.course.library.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Tag(name = "Выдачи", description = "Операции с выдачами книг")
@RestController
@RequestMapping("/api/loans")
@Validated
public class LoanController {

    private final Library library;
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public LoanController(Library library) {
        this.library = library;
    }

    @Operation(summary = "Получить все выдачи")
    @GetMapping
    public ResponseEntity<java.util.List<LoanResponse>> getAllLoans(
            @Parameter(description = "Показать только просроченные выдачи")
            @RequestParam(required = false) Boolean overdue) {
        
        LocalDate today = LocalDate.now();
        java.util.List<LoanResponse> responses = library.findAllLoans().stream()
                .map(loan -> new LoanResponse(
                        loan.id(),
                        loan.isbn().value(),
                        loan.cardNumber(),
                        loan.issuedOn(),
                        loan.dueOn(),
                        loan.returnedOn(),
                        loan.isOverdue(today)
                ))
                .collect(java.util.stream.Collectors.toList());
        
        if (Boolean.TRUE.equals(overdue)) {
            responses = responses.stream()
                    .filter(loan -> loan.overdue())
                    .toList();
        }
        
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Получить выдачу по номеру")
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable long id) {
        kg.course.library.Loan loan = library.findAllLoans().stream()
                .filter(l -> l.id() == id)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Выдача не найдена"));
        
        LoanResponse response = new LoanResponse(
                loan.id(),
                loan.isbn().value(),
                loan.cardNumber(),
                loan.issuedOn(),
                loan.dueOn(),
                loan.returnedOn(),
                loan.isOverdue(LocalDate.now())
        );
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Выдать книгу читателю")
    @PostMapping
    public ResponseEntity<LoanResponse> issueBook(@Validated @RequestBody IssueRequest request) {
        try {
            kg.course.library.Loan loan = library.issueBook(
                    request.cardNumber(),
                    new kg.course.library.Isbn(request.isbn()),
                    LocalDate.now()
            );
            
            LoanResponse response = new LoanResponse(
                    loan.id(),
                    loan.isbn().value(),
                    loan.cardNumber(),
                    loan.issuedOn(),
                    loan.dueOn(),
                    loan.returnedOn(),
                    loan.isOverdue(LocalDate.now())
            );
            
            return ResponseEntity.created(
                    org.springframework.web.servlet.support.ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(loan.id())
                            .toUri()
            ).body(response);
        } catch (BookUnavailableException e) {
            throw new ConflictException(e.getMessage());
        } catch (LimitExceededException e) {
            throw new ConflictException(e.getMessage());
        } catch (kg.course.library.OverdueException e) {
            throw new ConflictException(e.getMessage());
        }
    }

    @Operation(summary = "Вернуть книгу")
    @PostMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable long id) {
        kg.course.library.Loan loan = library.returnBook(id, LocalDate.now());
        
        LoanResponse response = new LoanResponse(
                loan.id(),
                loan.isbn().value(),
                loan.cardNumber(),
                loan.issuedOn(),
                loan.dueOn(),
                loan.returnedOn(),
                loan.isOverdue(LocalDate.now())
        );
        
        return ResponseEntity.ok(response);
    }
}