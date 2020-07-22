package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

//Return a list of the selected XML SERVICE_CODE;
public class XMLParser {
    private static final String XML1 = "<VSPClaimRequest><ReceivedDate>07/08/2020</ReceivedDate><BatchLocator>AF1</BatchLocator><BatchDate>07/09/2020</BatchDate><ClaimSource>7</ClaimSource><HCFA1500><InsuranceType><GroupHealthPlan></GroupHealthPlan></InsuranceType><Patient><IndividualName><First>CINDY</First><Last>LISKA</Last></IndividualName><Address><Street>2418 MEADOW DRIVE</Street><City>BUFFALO</City><State>MN</State><Zip>55313</Zip><ZipExtension></ZipExtension></Address><DateOfBirth>11/05/1962</DateOfBirth><Gender><Female></Female></Gender><RelationshipToInsured><Spouse></Spouse></RelationshipToInsured><Signature><Indicator><True></True></Indicator></Signature><AccountNumber>0014926363-01</AccountNumber></Patient><Insured><ID>3616</ID><IndividualName><First>JAMES</First><Last>LISKA</Last></IndividualName><Address><Street>2418 MEADOW DRIVE</Street><City>BUFFALO</City><State>MN</State><Zip>55313</Zip><ZipExtension>5313</ZipExtension></Address><Signature><Indicator><True></True></Indicator></Signature></Insured><ConditionRelatedTo><Employment><False></False></Employment><AutoAccident><False></False><State></State></AutoAccident><OtherAccident><False></False></OtherAccident></ConditionRelatedTo><Diagnosis>H52.10</Diagnosis><ClaimLine><ServiceDate><FromDate>06/25/2020</FromDate><ToDate>06/25/2020</ToDate></ServiceDate><Procedure><HCPCCode>V2020</HCPCCode></Procedure><DiagnosisCodePtr>1</DiagnosisCodePtr><Charges>139</Charges><Units>1</Units><RenderingNPI>1811187792</RenderingNPI></ClaimLine><ClaimLine><ServiceDate><FromDate>06/25/2020</FromDate><ToDate>06/25/2020</ToDate></ServiceDate><Procedure><HCPCCode>V2781</HCPCCode></Procedure><DiagnosisCodePtr>1</DiagnosisCodePtr><Charges>175</Charges><Units>2</Units><RenderingNPI>1811187792</RenderingNPI></ClaimLine><TotalCharge>314</TotalCharge><AmountPaid>121.2</AmountPaid><Practitioner><ID>1811187792</ID><IndividualName><First>TARGET</First><Last>OPTICAL</Last></IndividualName><FederalTaxIDNumber><EIN></EIN><Number>311339854</Number></FederalTaxIDNumber><AcceptAssignment><True></True></AcceptAssignment><BillingInfo><Name>LUXOTTICA RETAIL NORTH AMERICA</Name><Address><Street>LUXOTTICA RETAIL NORTH AMERICA</Street><Street2>14677 COLLECTIONS CENTER DR</Street2><City>CHICAGO</City><State>IL</State><Zip>60693</Zip><ZipExtension>9999</ZipExtension></Address></BillingInfo><Signature><Indicator><True></True></Indicator></Signature></Practitioner><Facility><Name>TARGET OPTICAL</Name><Address><Street>1447 E SEVENTH ST</Street><City>MONTICELLO</City><State>MN</State><Zip>55362</Zip><ZipExtension>9999</ZipExtension></Address></Facility></HCFA1500></VSPClaimRequest>";
    public static final String SERVICE_CODE = "HCFA1500.ClaimLine.Procedure.HCPCCode";

    private DocumentBuilderFactory factory;
    private DocumentBuilder dBuilder;

    public XMLParser() {
        try {
            factory = DocumentBuilderFactory.newInstance();
            dBuilder = factory.newDocumentBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }

    private List<String> getTokensWithCollection(String str, String delimiter) {
        return Collections.list(new StringTokenizer(str, delimiter)).stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
    }

    public List<String> getValues(String xml, String nodeName) {
        try {
            Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
            doc.getDocumentElement().normalize();
            List<String> tokens = getTokensWithCollection(nodeName, ".");
            NodeList nList = doc.getElementsByTagName(tokens.get(0));
            for (int i = 1; i < tokens.size(); i++) {
                nList = doc.getElementsByTagName(tokens.get(i));
            }
            List<String> output = new ArrayList<>();
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    output.add(nNode.getTextContent());
                }
            }
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
