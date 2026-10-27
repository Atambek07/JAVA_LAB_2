package kg.course.library.config;

import io.swagger.v3.oas.annotations.tags.Tag;
import kg.course.library.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Clock;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Configuration
public class LibraryConfig {

    @Bean
    public Library library() {
        return new Library();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public ApplicationRunner applicationRunner(Library library, 
                                              @Value("${library.import-file:books.csv}") String importFile,
                                              Clock clock) {
        return args -> {
            Resource resource = new ClassPathResource(importFile);
            if (!resource.exists()) {
                System.out.println("WARNING: Файл " + importFile + " не найден, библиотека будет пустой");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        String[] parts = line.split(";");
                        if (parts.length >= 5) {
                            String isbn = parts[0];
                            String title = parts[1];
                            java.util.List<String> authors = java.util.Arrays.stream(parts[2].split(","))
                                    .map(String::trim)
                                    .collect(Collectors.toList());
                            int year = Integer.parseInt(parts[3]);
                            int copies = Integer.parseInt(parts[4]);
                            
                            Book book = new Book(new Isbn(isbn), title, authors, year);
                            library.addBook(book, copies);
                        }
                    } catch (Exception e) {
                        System.out.println("WARNING: Ошибка импорта строки: " + line + " - " + e.getMessage());
                    }
                }
            }

            // Register three readers of different categories
            library.registerReader(new Reader("R-10001", "Студент Иванов", ReaderCategory.STUDENT));
            library.registerReader(new Reader("R-10002", "Преподаватель Петров", ReaderCategory.TEACHER));
            library.registerReader(new Reader("R-10003", "Гость Сидоров", ReaderCategory.GUEST));
        };
    }
}