package org.example.Classes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

class LogsReaderTest {
    
    @Test
    void confirmFileExists() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        LogsReader.logsReader("nonexistent-file.txt");

        System.setOut(originalOut); // restore
        assertTrue(outContent.toString().contains("Error reading log file"));
    }

    @Test 
    void fileIsTxt(){
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        LogsReader.logsReader("logs.tx");

        System.setOut(originalOut); // restore
        assertTrue(outContent.toString().contains("The file must be a .txt file."));
    }

    @Test
    void trueForFile() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        LogsReader.logsReader("logs.txt");

        System.setOut(originalOut); // restore
        assertFalse(outContent.toString().contains("Error reading log file"));
    }


}
