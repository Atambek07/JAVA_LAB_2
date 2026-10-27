package kg.course.library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("############################################################");
        System.out.println("#                 ЛАБОРАТОРНАЯ РАБОТА № 1                  #");
        System.out.println("############################################################ ");

        System.out.println("========== ЛАБ 1: ЗАДАНИЕ 1. Базовые конструкции ==========");
        int[] arr = {10, -5, 23, 7, 0, 14};
        System.out.println("Исходный массив: " + Arrays.toString(arr));
        System.out.println("Среднее арифметическое: " + LibraryUtils.average(arr));
        System.out.println("Максимальный элемент: " + LibraryUtils.max(arr));
        System.out.println("Отсортированная копия: " + Arrays.toString(LibraryUtils.sortedCopy(arr)));
        System.out.println("Нормализация '0555 123 456': " + LibraryUtils.normalizePhone("0555 123 456"));

        System.out.println(" ========== ЛАБ 1: ЗАДАНИЕ 2 и 5. ReaderCard и сравнение ==========");
        ReaderCard card1 = new ReaderCard("R-12345", "Иван Иванов", 5);
        ReaderCard card2 = new ReaderCard("R-12345", "Петр Петров", 3);
        card1.take();
        System.out.println("Билет 1: " + card1);
        System.out.println("card1 == card2: " + (card1 == card2));
        System.out.println("card1.equals(card2): " + card1.equals(card2));
        Set<ReaderCard> cards = new HashSet<>(List.of(card1, card2));
        System.out.println("Размер множества HashSet: " + cards.size());

        System.out.println(" ========== ЛАБ 1: ЗАДАНИЕ 3 и 4. Наследование и Полиморфизм ==========");
        Item[] items = {
            new PaperBook("Java 21", 2024, "Герберт Шилдт", 1200),
            new PaperBook("Чистый код", 2010, "Роберт Мартин", 464),
            new Magazine("Хакер", 2023, 15)
        };
        int bookCount = 0;
        for (Item item : items) {
            System.out.println("-> " + item.describe());
            if (item instanceof PaperBook) bookCount++;
        }
        System.out.println("Количество книг (PaperBook): " + bookCount);

        System.out.println(" ========== ЛАБ 1: ЗАДАНИЕ 6. Перечисления и Записи ==========");
        Isbn isbn1 = new Isbn("978-5-4461-0914-9");
        Isbn isbn2 = new Isbn("9785446109149");
        System.out.println("Записи Isbn равны: " + isbn1.equals(isbn2));


        System.out.println("############################################################");
        System.out.println("#                 ЛАБОРАТОРНАЯ РАБОТА № 2                  #");
        System.out.println("############################################################ ");

        Library library = new Library();

        System.out.println("========== ЛАБ 2: ЗАДАНИЕ 6. Импорт книг из файла books.csv ==========");
        BookCsvImporter importer = new BookCsvImporter();
        try {
            ImportResult importResult = importer.importBooks(Path.of("books.csv"));
            for (ImportedBook ib : importResult.imported()) {
                library.addBook(ib.book(), ib.copies());
            }
            System.out.println("Импортировано книг: " + importResult.imported().size());
            System.out.println("Список ошибок (" + importResult.errors().size() + "):");
            for (String err : importResult.errors()) {
                System.out.println("  [Ошибка] " + err);
            }
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл books.csv: " + e.getMessage());
        }

        System.out.println(" ========== ЛАБ 2: ЗАДАНИЯ 2 и 3. Каталог и Читатели ==========");
        library.registerReader(new Reader("R-00001", "Нуртилек Калыгулов", ReaderCategory.STUDENT));
        library.registerReader(new Reader("R-00002", "Алексей Смирнов", ReaderCategory.GUEST));
        library.registerReader(new Reader("R-00003", "Иван Петров", ReaderCategory.TEACHER));
        System.out.println("Всего книг в каталоге: " + library.findAllBooks().size());
        System.out.println("Зарегистрировано читателей: " + library.findAllReaders().size());

        System.out.println(" ========== ЛАБ 2: ЗАДАНИЕ 4. Выдача, возврат и 5 ошибочных ситуаций ==========");
        Isbn effectiveJava = new Isbn("978-5-4461-0914-9"); // 2 экз.
        Isbn warAndPeace = new Isbn("978-5-17-090630-2");   // 1 экз.
        Isbn cleanCode = new Isbn("978-5-4461-0960-6");     // 1 экз.
        Isbn coreJava = new Isbn("978-5-4461-1101-2");      // 3 экз.

        LocalDate issueDate = LocalDate.of(2026, 3, 1);

        library.issueBook("R-00001", effectiveJava, issueDate); // Студенту до 15 марта
        library.issueBook("R-00002", warAndPeace, issueDate);   // Гостю (лимит 1) до 8 марта
        library.issueBook("R-00003", cleanCode, issueDate);     // Преподавателю до 31 марта
        library.issueBook("R-00001", coreJava, issueDate);      // Вторая книга студенту
        System.out.println("Успешно оформлено выдач: " + library.findAllLoans().size());

        // 1. Читатель не зарегистрирован -> NotFoundException
        try {
            library.issueBook("R-99999", effectiveJava, issueDate);
        } catch (LibraryException e) {
            System.out.println("1. [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }

        // 2. Книга отсутствует в каталоге -> NotFoundException
        try {
            library.issueBook("R-00001", new Isbn("978-0-00-000000-0"), issueDate);
        } catch (LibraryException e) {
            System.out.println("2. [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }

        // 3. У читателя есть просроченные выдачи -> OverdueException
        try {
            library.issueBook("R-00001", coreJava, LocalDate.of(2026, 3, 20));
        } catch (LibraryException e) {
            System.out.println("3. [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }

        // 4. Превышен лимит книг категории -> LimitExceededException
        try {
            library.issueBook("R-00002", coreJava, issueDate);
        } catch (LibraryException e) {
            System.out.println("4. [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }

        // 5. Нет свободных экземпляров книги -> BookUnavailableException
        try {
            library.issueBook("R-00001", cleanCode, issueDate);
        } catch (LibraryException e) {
            System.out.println("5. [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }

        System.out.println(" ========== ЛАБ 2: ЗАДАНИЕ 5. Отчёты на Stream API ==========");
        LibraryReports reports = new LibraryReports(library);
        LocalDate checkDate = LocalDate.of(2026, 3, 20);

        System.out.println("Книги со словом 'java' в названии:");

        reports.searchByTitle("java").forEach(b -> System.out.println("  - " + b.title()));

        System.out.println("Просроченные выдачи на " + checkDate + ": " + reports.overdueLoans(checkDate).size() + " шт.");
        System.out.println("Количество выдач по категориям: " + reports.loansByCategory());
        System.out.println("Все авторы (TreeSet): " + reports.allAuthors());
        System.out.println("Топ-3 книги по числу выдач: " + reports.topBooks(3));

        System.out.println(" ========== ЛАБ 2: ЗАДАНИЕ 7. Запись отчёта в файл overdue.csv ==========");
        Path overdueFile = Path.of("overdue.csv");
        ReportWriter writer = new ReportWriter();
        try {
            writer.writeOverdueReport(library, checkDate, overdueFile);
            System.out.println("Файл overdue.csv сформирован! Его содержимое:");
            Files.readAllLines(overdueFile).forEach(line -> System.out.println("  " + line));
        } catch (IOException e) {
            System.out.println("Ошибка при записи файла overdue.csv: " + e.getMessage());
        }
    }
}
