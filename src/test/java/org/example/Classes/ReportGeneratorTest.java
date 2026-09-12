package org.example.Classes;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReportGeneratorTest {

    @Test
    void malformedLinesAppearInReport() {
        // Arrange
        List<LogsReader.LineIssue> malformedLines = List.of(
            new LogsReader.LineIssue(23, "2024-03-15 02:17:00 | WARN | 203.0.113.42 | /login")
        );

        // Act
        String report = ReportGenerator.buildReport(malformedLines);

        // Assert
        
        // TODO: check report contains "--- Malformed Lines ---"
        // TODO: check report contains "Line 23: 2024-03-15 02:17:00 | WARN | 203.0.113.42 | /login"
    }
}
