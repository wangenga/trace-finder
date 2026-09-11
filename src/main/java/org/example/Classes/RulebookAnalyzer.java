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





    //CORRECTLY STRUCTURED LOGS HAVE 4 DELIMETERS. I NEED THE CHARACTERS BETWEEN DELIMETER 1 AND 2.

    List<String> flaggedLogs = new ArrayList<>();
    List<String> unknownLogs = new ArrayList<>();
    List<String> flaggedIpsList = new ArrayList<>();
    Set<String> flaggedIPsUnique = new HashSet<>(); //Uniquely stores IP Addresses that have been flagged. We will then use that dataset to see how many times...
    //...each of these records appears in flagged logs.
    Map<String, Integer> flaggedIPsCount = new HashMap<>();
    Map<String, Integer> stats = new HashMap<>();


    int warnCount = 0;
    int infoCount = 0;
    int errorCount = 0;
    int alertCount = 0;

    public void checkLevel (List<String> validEntries, List<String> numberedValidEntries){


        for (int i = 0; i < validEntries.size(); i++) {
            String currentLog = validEntries.get(i);

            int delimeter1Index = currentLog.indexOf("|" );
            int delimeter2Index = currentLog.indexOf("|" , delimeter1Index + 1);

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
            int d1 = flaggedLog.indexOf("|");
            int d2 = flaggedLog.indexOf("|", d1 + 1);
            int d3 = flaggedLog.indexOf("|", d2 + 1);

            String flaggedIP = flaggedLog.substring(d2 + 1, d3).trim();
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

    //Move valid file path logic to commandValidator
    public Map<String, Integer> getStats(){
        stats.put("INFO", infoCount);
        stats.put("WARN", warnCount);
        stats.put("ERROR", errorCount);
        stats.put("ALERT", alertCount);

        System.out.println(stats);
        return stats;
    }

    public Map<String,Integer> getSuspiciousIp (){
        Map<String, Integer> sortedFlaggedIPsCount = flaggedIPsCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, // Merge function (not needed here but required by syntax)
                        LinkedHashMap::new                // Guarantees the sorted order is kept
                ));

        System.out.println("Sorted map: " + sortedFlaggedIPsCount);
        return sortedFlaggedIPsCount;
    }

    public List<String> getFlaggedEntries (){
        return flaggedLogs;
    }
    public List<String> getUnknownLogs(){
        return unknownLogs;
    }
}

