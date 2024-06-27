package dev.alexcoss.carservice.standalone;

import dev.alexcoss.carservice.service.CsvCarService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("standalone")
public class StartupRunner {
    private static final String FILE_PATH = "src/main/resources/csv/small_file.csv";

    private final CsvCarService csvCarService;

    @PostConstruct
    public void run() {
        csvCarService.parseAndSaveCars(FILE_PATH);
    }
}
