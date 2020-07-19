package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BRNumFileBuilder {
    public static final String PAT_DOB = "Patient.DateOfBirth";
    public static final String PAT_FNAME = "Patient.IndividualName.First";
    public static final String PAT_MI = "Patient.IndividualName.MiddleInitial";
    public static final String PAT_LNAME = "Patient.IndividualName.Last";
    public static final String PAT_ST = "Patient.Address.Street";
    public static final String PAT_CITY = "Patient.Address.City";
    public static final String PAT_STATE = "Patient.Address.State";
    public static final String PAT_ZIP = "Patient.Address.Zip";

    public static final String MBR_ID = "Insured.ID";
    public static final String MBR_DOB = "Insured.DateOfBirth";
    public static final String MBR_FNAME = "Insured.IndividualName.First";
    public static final String MBR_MI = "Insured.IndividualName.MiddleInitial";
    public static final String MBR_LNAME = "Insured.IndividualName.Last";
    public static final String MBR_ST = "Insured.Address.Street";
    public static final String MBR_CITY = "Insured.Address.City";
    public static final String MBR_STATE = "Insured.Address.State";
    public static final String MBR_ZIP = "Insured.Address.Zip";

    public static final String PROV_FNAME = "HCFA1500.Practitioner.IndividualName.First";
    public static final String PROV_LNAME = "HCFA1500.Practitioner.IndividualName.Last";
    public static final String PROV_TAX_ID = "HCFA5100.Practitioner.FederalTaxIDNumber.Number";
    public static final String CLM_BR_NBR = "BasicForm.BenefitRequestNumber";
    public static final String CLM_PROC_DCN = "DCN";
    public static final String PAT_ACCT_NBR = "HCFA5100.Patient.AccountNumber";


    private static final String[] DEFAULT_ELEMENT_NAMES = new String[]{PAT_DOB, PAT_FNAME, PAT_MI, PAT_LNAME, PAT_ST, PAT_CITY, PAT_STATE, PAT_ZIP, MBR_FNAME, MBR_MI, MBR_LNAME, MBR_ST, MBR_CITY, MBR_STATE, MBR_ZIP, PROV_FNAME, PROV_LNAME, PROV_TAX_ID, CLM_BR_NBR, CLM_PROC_DCN, PAT_ACCT_NBR, MBR_ID, MBR_DOB};
    private static Logger log = LoggerFactory.getLogger(BRNumFileBuilder.class);

    private FileWriter outputFile;
    private int fileCounter = 0;

    public BRNumFileBuilder(String inputFileName) {
        try {
            outputFile = createFileWriter(inputFileName);
        } catch (IOException e) {
            log.error("Error: ", e);
        }
    }

    private FileWriter createFileWriter(String inputFileName) throws IOException {
        int lastIndex = inputFileName.lastIndexOf(File.separator);
        String path = inputFileName.substring(0, lastIndex + 1);
        String nuFileName = path + inputFileName.substring(lastIndex + 1, inputFileName.lastIndexOf(".")) + "_" + ++fileCounter + ".txt";
        return new FileWriter(new File(nuFileName));
    }

    public static void main(String[] args) {
        if (!checkArgs(args)) {
            return;
        }
        BRNumFileBuilder fileBuilder = new BRNumFileBuilder(args[0]);

        try {
            XmlScraper myScraper = new XmlScraper();
            BufferedReader inputFile = new BufferedReader(new FileReader(args[0]));
            String line = inputFile.readLine();
            List<String> elementNames = fileBuilder.createElementNames(args);
            int lineCounter = 0;

            while (line != null) {
                List<String> values = myScraper.getValues(line, elementNames);
                String sql = createOutputString(values, "");
                lineCounter++;

                fileBuilder.writeText(sql);
                line = inputFile.readLine();
            }
            fileBuilder.closeFiles();
            System.out.println(String.format("%s XmlLinesRead: %d", args[0], lineCounter));
        } catch (Exception e) {
            fileBuilder.writeText(e.getMessage());
            log.error("Error: ", e);
        }
    }

    private static boolean checkArgs(String[] args) {
        if (args.length == 0) {
            System.out.println("Please pass in a file name & search criteria, ie c:/mydir/my.xml DCN");
            return false;
        }
        return true;
    }

    private static String createOutputString(List<String> values, String whereCriteria) {
        return String.format("UPDATE PVSP001.CLM2325T SET PAT_DOB='%s', PAT_F_NM='%s', PAT_M_INI='%s', PAT_L_NM='%s', PAT_LN1_ADR='%s', PAT_CTY_ADR='%s', PAT_ST_ADR='%s', PAT_ZIP_ADR='%s', MEMBR_F_NM='%s', MEMBR_M_INI='%s', MEMBR_L_NM='%s', MEMBR_LN1_ADR='%s', MEMBR_CTY_ADR='%s', MEMBR_ST_ADR='%s', MEMBR_ZIP_ADR='%s', RND_PROV_F_NM='%s', RND_PROV_L_NM='%s', PRC_DPA_TAX_ID='%s'", values.toArray(new String[]{}));
    }

    private List<String> createElementNames(String[] inputArgs) {
        if (inputArgs.length < 3) {
            return Arrays.asList(DEFAULT_ELEMENT_NAMES);
        }
        List<String> elementNames = new ArrayList<>();
        for (int i = 0; i < inputArgs.length; i++) {
            if (i != 0) {
                elementNames.add(inputArgs[i]);
            }
        }
        return elementNames;
    }

    private void writeText(String message) {
        try {
            if (message != null) {
                outputFile.write(message);
                outputFile.write(System.lineSeparator());
                System.out.println(message);
            }
        } catch (IOException e) {
            log.error("Error: ", e);
        }
    }

    private void closeFiles() {
        try {
            outputFile.close();
        } catch (IOException e) {
            log.error("Error: ", e);
        }
    }
}
