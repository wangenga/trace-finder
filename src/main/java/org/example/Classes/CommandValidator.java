package org.example.Classes;

public class CommandValidator {
    //Main class calls this class file.
    //Its workflow is that it takes the command, cleans it, and ensures that 3 files are part of the argument.
    // If there are problems, relevant exceptions are called.
    // If no problem, it has a method to return the 3 file names to the main class.

    String [] paths = new String[3];
    public void getArguments (String[] arguments){
        paths = arguments;
    }

    //Ensure the correct order of the 3 files: .txt, .csv and .txt
    public boolean correctOrder (){
        if (paths[0].endsWith(".txt") && paths[1].endsWith(".csv") && paths[2].endsWith(".txt")){
            System.out.println("Correct order");
            return true;
        }
        else {
            System.out.println("Ensure the correct order, txt, csv, txt");
            return false;
        }
    }

}
