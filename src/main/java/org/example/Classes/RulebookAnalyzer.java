package org.example.Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class RulebookAnalyzer {
    //Ensures rulebook can be accessed and read, else calls the exception

    //Recieves the dataset of well structured lines from the logs
    //Compares each line against the rulebook

    //Gives each line a score and stores that info in a dataset. eg a multidimensional array or sth
    //Hands this data to the main class.

    String ruleBook;
    public void getRulebookPath (String path){
        System.out.println("The rulebook path is " + path);
        ruleBook = path;
    }

    //Check if rulebook exists and has expected columns
    public boolean validRulebook (){
        if (Files.exists(Paths.get(ruleBook))){
            //Expected columns:
            List<String> expectedHeaders = Arrays.asList("level", "severity_score");

            try (BufferedReader br = new BufferedReader(new FileReader(ruleBook))){
                String header = br.readLine();

                if(header != null){
                    List<String> actualHeaders = Arrays.stream(header.split(","))
                            .map(String::trim)
                            .toList();

                    if (expectedHeaders.equals(actualHeaders)){
                        System.out.println("Nice! The rulebook has correct headers: " + actualHeaders);
                        return true;
                    }
                    else {
                        System.out.println("The column names/headers of this file do not match what is expected!!");
                        return false;
                    }
                }
                else {
                    System.out.println("This csv file is empty!!");
                    return false;
                }
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }
        }
        else{
            System.out.println("This rulebook does not exist.");
            return false;
        }
    }

}
