package org.example.Classes;

public class LogsReader {
    //Needs to retrieve the file path from the main class. It could get it from command validator class directly but the instructions are that Main Orchestrates
    //If it doesn't exist or can't be read, call the exception
    //Tracks malformed lines and also the correctly structured lines.
    //Returns both datasets to the main class.
    //How to check well structured logs:
    //Use string builder, and separate the logs where there is a delimeter |. If it doesn't exist, then the wrong delimeter was used.
    //After splitting each part of the log, check that it has all the required info.
}
