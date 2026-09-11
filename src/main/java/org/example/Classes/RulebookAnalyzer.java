package org.example.Classes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


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


    //CORRECTLY STRUCTURED LOGS HAVE 4 DELIMETERS. I NEED THE CHARACTERS BETWEEN DELIMETER 1 AND 2.

    List<String> flaggedLogs = new ArrayList<>();
    List<String> unknownLogs = new ArrayList<>();
    List<String> flaggedIpsList = new ArrayList<>();
    Set<String> flaggedIPsUnique = new HashSet<>(); //Uniquely stores IP Addresses that have been flagged. We will then use that dataset to see how many times...
    //...each of these records appears in flagged logs.
    Map<String, Integer> flaggedIPsCount = new HashMap<>();


    int warnCount = 0;
    int infoCount = 0;
    int errorCount = 0;
    int alertCount = 0;

    int delimeter1Index;
    int delimeter2Index;
    int delimeter3Index;

    public void checkLevel (List<String> validEntries, List<String> numberedValidEntries){


        for (int i = 0; i < validEntries.size(); i++) {
            String currentLog = validEntries.get(i);

            delimeter1Index = currentLog.indexOf("|" );
            delimeter2Index = currentLog.indexOf("|" , delimeter1Index + 1);

            String severity = currentLog.substring(delimeter1Index + 1, delimeter2Index).trim();
            if (severity.equals("WARN") || severity.equals("ERROR") || severity.equals("ALERT")){
                flaggedLogs.add(currentLog);
                if (severity.equals("WARN")){
                    warnCount ++;
                }
                else if (severity.equals("ERROR")){
                    errorCount ++;
                }
                else {
                    alertCount ++;
                }
            }
            else if (severity.equals("INFO")){
                infoCount++;
            }
            else {
                //Since numbered valid logs and valid logs are the same size, we can just replace the current log with the
                //log at numberedValidLog[i] and add that into unknownLog.
                currentLog = numberedValidEntries.get(i);
                unknownLogs.add(currentLog);
            }

        }
        System.out.println("The flagged logs are: ");
        for (String log : flaggedLogs) {
            System.out.println(log);
        }
        System.out.println("The unknown logs are: ");
        for (String log : unknownLogs) {
            System.out.println(log);
        }

        System.out.println("Warn: " + warnCount);
        System.out.println("Info: " + infoCount);
        System.out.println("Error: " + errorCount);
        System.out.println("Alert: " + alertCount);
    }


    public void suspiciousIPs (){
        for (String flaggedLog : flaggedLogs){
            delimeter3Index = flaggedLog.indexOf("|" , delimeter2Index + 1);
            String flaggedIP = flaggedLog.substring(delimeter2Index + 1, delimeter3Index).trim();
            //We add the IP adresses to a normal list and to a unique list, and count how many times a unique record appears:
            //**Probably not the most optimal solution.
            flaggedIPsUnique.add(flaggedIP);
            flaggedIpsList.add(flaggedIP);
        }

        for (String ip : flaggedIPsUnique){
            int count = Collections.frequency(flaggedIpsList, ip);
            flaggedIPsCount.put(ip, count);
        }


        System.out.println("The flagged IPs are: ");
        for (String flaggedIp : flaggedIPsUnique){
            System.out.println(flaggedIp);
        }

        System.out.println(flaggedIPsCount);
    }

    public int handWarnCount () {
        return warnCount;
    }
    public int handInfoCount (){
        return infoCount;
    }
    public int handErrorCount (){
        return errorCount;
    }
    public int handAlertCount (){
        return alertCount;
    }
    public List<String> handFlaggedEntries (){
        return flaggedLogs;
    }
    public Map<String,Integer> handSuspiciousActivityByIp (){
        return flaggedIPsCount;
    }
    public List<String> handUnknownLogs (){
        return unknownLogs;
    }
}

