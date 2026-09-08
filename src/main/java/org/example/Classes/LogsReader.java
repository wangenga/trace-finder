package org.example.Classes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class LogsReader {
    public static void logsReader (){
        Path logfilePath = Path.of("logs.txt");

        try (Stream<String> logsLines = Files.lines(logfilePath)) {

        logsLines.forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("Error reading log file: " + e.getMessage());
        }
    }
    //Needs to retrieve the file path from the main class. It could get it from command validator class directly but the instructions are that Main Orchestrates
    //If it doesn't exist or can't be read, call the exception
    //Tracks malformed lines and also the correctly structured lines.
    //Returns both datasets to the main class.
    //
}
