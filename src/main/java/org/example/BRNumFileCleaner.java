package org.example;

import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BRNumFileCleaner {
    public static final String CLM_BR_NBR = "BasicForm.BenefitRequestNumber";

    private FileWriter outputFileCSV;
    private FileWriter outputFileXML;
    private BufferedReader inputBufferOldVsNew;
    private BufferedReader inputBufferClaims;
    private BufferedReader inputBufferXML;
    private String inputBufferOldVsNewFileName;
    private String inputBufferClaimsFileName;
    private String inputBufferXMLFileName;

    public BRNumFileCleaner(String[] fileNames) {
        try {
            outputFileCSV = createFileWriter(fileNames[1], "csv");
            outputFileXML = createFileWriter(fileNames[2], "xml");
            inputBufferOldVsNew = new BufferedReader(new FileReader(fileNames[0]));
            inputBufferClaims = new BufferedReader(new FileReader(fileNames[1]));
            inputBufferXML = new BufferedReader(new FileReader(fileNames[2]));
            inputBufferOldVsNewFileName = fileNames[0];
            inputBufferClaimsFileName = fileNames[1];
            inputBufferXMLFileName = fileNames[2];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader getInputBufferOldVsNew() {
        return inputBufferOldVsNew;
    }

    public BufferedReader getInputBufferClaims() {
        return inputBufferClaims;
    }

    public BufferedReader getInputBufferXML() {
        return inputBufferXML;
    }

    public String getInputBufferOldVsNewFileName() {
        return inputBufferOldVsNewFileName;
    }

    public String getInputBufferClaimsFileName() {
        return inputBufferClaimsFileName;
    }

    public String getInputBufferXMLFileName() {
        return inputBufferXMLFileName;
    }

    private FileWriter createFileWriter(String inputFileName, String fileExtension) throws IOException {
        int lastIndex = inputFileName.lastIndexOf(File.separator);
        String path = inputFileName.substring(0, lastIndex + 1);
        String nuFileName = path + inputFileName.substring(lastIndex + 1, inputFileName.lastIndexOf(".")) + "_output." + fileExtension;
        return new FileWriter(new File(nuFileName));
    }

    public static void main(String[] args) {
        if (!checkArgs(args)) {
            return;
        }
        BRNumFileCleaner fileBuilder = new BRNumFileCleaner(args);
        try {
            HashMap<String, List<String>> old2NuMap = buildHashMap(fileBuilder);
            old2NuMap = compareAndOutputCSV(old2NuMap, fileBuilder);
            compareAndOutputXML(old2NuMap, fileBuilder);
            fileBuilder.closeFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, List<String>> buildHashMap(BRNumFileCleaner fileBuilder) {
        HashMap<String, List<String>> old2NuMap = new HashMap<>();
        int lineCounter = 0;
        try {
            String line = fileBuilder.getInputBufferOldVsNew().readLine();
            while (line != null) {
                List<String> tokens = getTokensWithCollection(line);
                String oldBR = tokens.get(1).trim();
                List<String> entries = new ArrayList<>();
                if (!StringUtils.isBlank(oldBR)) {
                    entries.add(line);
                    if (old2NuMap.containsKey(oldBR)) {
                        old2NuMap.get(oldBR).add(line);
                    } else {
                        old2NuMap.put(oldBR, entries);
                    }
                }
                lineCounter++;
                line = fileBuilder.getInputBufferOldVsNew().readLine();
            }
            System.out.println(String.format("%s CsvLinesRead: %d", fileBuilder.getInputBufferOldVsNewFileName(), lineCounter));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return old2NuMap;
    }

    public static HashMap<String, List<String>> compareAndOutputCSV(HashMap<String, List<String>> old2NuMap, BRNumFileCleaner fileBuilder) {
        int lineCounter = 0;
        HashMap<String, List<String>> outputLinesMap = new HashMap<>();
        try {
            BufferedReader reader = fileBuilder.getInputBufferClaims();
            String line = reader.readLine();
            while (line != null) {
                List<String> tokens = getTokensWithCollection(line);
                String oldBR = tokens.get(2).trim();
                if (old2NuMap.containsKey(oldBR)) {
                    outputLinesMap.put(oldBR, old2NuMap.get(oldBR));
                }
                lineCounter++;
                line = reader.readLine();
            }
            fileBuilder.writeCSVText(outputLinesMap);
            System.out.println(String.format("%s CsvLinesRead: %d", fileBuilder.getInputBufferClaimsFileName(), lineCounter));
        } catch (Exception e) {
            System.out.println(lineCounter);
            e.printStackTrace();
        }
        return outputLinesMap;
    }

    public static void compareAndOutputXML(HashMap<String, List<String>> old2NuMap, BRNumFileCleaner fileBuilder) {
        int lineCounter = 0;
        try {
            XmlScraper myScraper = new XmlScraper();
            BufferedReader reader = fileBuilder.getInputBufferXML();
            String line = reader.readLine();
            while (line != null) {
                String brNum = myScraper.getValue(line, CLM_BR_NBR);
                if (old2NuMap.containsKey(brNum)) {
                    fileBuilder.writeXMLText(line);
                }
                lineCounter++;
                line = reader.readLine();
            }
            System.out.println(String.format("%s XmlLinesRead: %d", fileBuilder.getInputBufferXMLFileName(), lineCounter));
        } catch (Exception e) {
            System.out.println(lineCounter);
            e.printStackTrace();
        }
    }

    public static List<String> getTokensWithCollection(String str) {
        return Collections.list(new StringTokenizer(str, ",")).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
    }

    private static boolean checkArgs(String[] args) {
        if (args.length == 0) {
            System.out.println("Please pass in a file name & search criteria, ie c:/mydir/my.xml DCN");
            return false;
        }
        return true;
    }

    private void writeCSVText(HashMap<String, List<String>> outputLines) {
        if (outputLines != null) {
            outputLines.values().forEach(l -> writeCSVText(l));
        }
    }

    private void writeCSVText(List<String> message) {
        if (message != null) {
            message.forEach(m -> writeCSVText(m));
        }
    }

    private void writeCSVText(String message) {
        try {
            if (message != null) {
                outputFileCSV.write(message);
                outputFileCSV.write(System.lineSeparator());
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeXMLText(String message) {
        try {
            if (message != null) {
                outputFileXML.write(message);
                outputFileXML.write(System.lineSeparator());
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeFiles() {
        try {
            outputFileCSV.close();
            outputFileXML.close();
            inputBufferOldVsNew.close();
            inputBufferClaims.close();
            inputBufferXML.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
