package kg.course.library.web;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record LoanResponse(
        @Schema(description = "Номер выдачи")
        long id,
        
        @Schema(description = "ISBN книги")
        String isbn,
        
        @Schema(description = "Номер билета читателя")
        String cardNumber,
        
        @Schema(description = "Дата выдачи")
        LocalDate issuedOn,
        
        @Schema(description = "Срок возврата")
        LocalDate dueOn,
        
        @Schema(description = "Дата возврата (null если не возвращена)")
        LocalDate returnedOn,
        
        @Schema(description = "Просрочена ли выдача")
        boolean overdue) {
}