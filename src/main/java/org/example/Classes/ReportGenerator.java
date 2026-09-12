package org.example.Classes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportGenerator {
   //Recieves data of each log and its score from main
    //Generates a report.
    //Checks if the output path can be created or written to.
    //If it cant, call an exception that kills the program.
    //Returns this report to the main class.

    private static final DateTimeFormatter REPORT_TIMESTAMP_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String buildReport(List<LogsReader.LineIssue> malformedLines){
        StringBuilder sb = new StringBuilder();

        sb.append("=== Log Analysis Report ===\n");
        sb.append("Generated: ").append(LocalDateTime.now().format(REPORT_TIMESTAMP_FORMAT)).append("\n\n");

        sb.append("--- Activity Summary ---\n");
        sb.append("TODO\n\n");

        sb.append("--- Flagged Entries ---\n");
        sb.append("TODO\n\n");

        sb.append("--- Suspicious Activity by IP ---\n");
        sb.append("TODO\n\n");

        sb.append("--- Unknown Patterns ---\n");
        sb.append("TODO\n\n");

        sb.append("--- Malformed Lines ---\n");
        if (malformedLines.isEmpty()){
            sb.append("None found\n");
        }else {
                for (LogsReader.LineIssue issue : malformedLines){
                    sb.append("Line ").append(issue.lineNumber()).append(": ").append(issue.rawContent()).append("\n");
                }
            }

        return sb.toString();

    }
}
