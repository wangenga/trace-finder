package org.example;

import org.example.Classes.LogsReader;

//import org.example.Classes.LogsReader;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    //Orchestrator. Hands and recieves data across classes and calls methods within each class.
    //As a user runs the program, it takes the input from the terminal and sends it to a method in CommandValidatorClass, maybe a setter method.
    //From that, the class is able to strip the command and ensure all arguments exist, and then prepares them into 3, the access log, the rulebook and the report.


    //It returns that data to main class.

    //Main then sends the access logs path to LogsReader.
    //LogsReader class reads the logs and makes sure it exists and can be read.
    //If so, it tracks and separates malformed lines from correctly structured ones.
    //It returns the correctly structured lines to MAIN class.

    //Main class then sends the data structure of correctly structured lines from the access logs to RulebookAnalyzer class.
    //It also sends it a file to the rulebook from the response it got from commandValidator.

    //Rulebook analyzer checks that the rulebook exists and can be read and then gives appropriate scores to the structured lines from the access logs.
    //It returns to main this list of structured access logs with their scores or 'Unknown patterns'


    //Main takes that data from Rulebook analyser and gives it to report generator.
    //It also sends the filepath of the report to the class.
    //ReportGenerator now reads the file path and ensures that the file can be created or written to (Updated), then using the data from rulebook analyzer, writes a report and sends it back to main
    //The report is printed.

    //Lifecycle: Main -> Command validator -> Main -> Logs reader -> Main -> Rulebook analyzer -> Main -> Report generator -> Main

    public static void main(String[] args) {
     
        LogsReader.logsReader();

    }
}
