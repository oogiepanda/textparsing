package org.example;

import org.junit.Test;

import java.util.List;

public class XMLParserTest {
    private static final String XML1 = "<VSPClaimRequest><ReceivedDate>07/08/2020</ReceivedDate><BatchLocator>AF1</BatchLocator><BatchDate>07/09/2020</BatchDate><ClaimSource>7</ClaimSource><HCFA1500><InsuranceType><GroupHealthPlan></GroupHealthPlan></InsuranceType><Patient><IndividualName><First>CINDY</First><Last>LISKA</Last></IndividualName><Address><Street>2418 MEADOW DRIVE</Street><City>BUFFALO</City><State>MN</State><Zip>55313</Zip><ZipExtension></ZipExtension></Address><DateOfBirth>11/05/1962</DateOfBirth><Gender><Female></Female></Gender><RelationshipToInsured><Spouse></Spouse></RelationshipToInsured><Signature><Indicator><True></True></Indicator></Signature><AccountNumber>0014926363-01</AccountNumber></Patient><Insured><ID>3616</ID><IndividualName><First>JAMES</First><Last>LISKA</Last></IndividualName><Address><Street>2418 MEADOW DRIVE</Street><City>BUFFALO</City><State>MN</State><Zip>55313</Zip><ZipExtension>5313</ZipExtension></Address><Signature><Indicator><True></True></Indicator></Signature></Insured><ConditionRelatedTo><Employment><False></False></Employment><AutoAccident><False></False><State></State></AutoAccident><OtherAccident><False></False></OtherAccident></ConditionRelatedTo><Diagnosis>H52.10</Diagnosis><ClaimLine><ServiceDate><FromDate>06/25/2020</FromDate><ToDate>06/25/2020</ToDate></ServiceDate><Procedure><HCPCCode>V2020</HCPCCode></Procedure><DiagnosisCodePtr>1</DiagnosisCodePtr><Charges>139</Charges><Units>1</Units><RenderingNPI>1811187792</RenderingNPI></ClaimLine><ClaimLine><ServiceDate><FromDate>06/25/2020</FromDate><ToDate>06/25/2020</ToDate></ServiceDate><Procedure><HCPCCode>V2781</HCPCCode></Procedure><DiagnosisCodePtr>1</DiagnosisCodePtr><Charges>175</Charges><Units>2</Units><RenderingNPI>1811187792</RenderingNPI></ClaimLine><TotalCharge>314</TotalCharge><AmountPaid>121.2</AmountPaid><Practitioner><ID>1811187792</ID><IndividualName><First>TARGET</First><Last>OPTICAL</Last></IndividualName><FederalTaxIDNumber><EIN></EIN><Number>311339854</Number></FederalTaxIDNumber><AcceptAssignment><True></True></AcceptAssignment><BillingInfo><Name>LUXOTTICA RETAIL NORTH AMERICA</Name><Address><Street>LUXOTTICA RETAIL NORTH AMERICA</Street><Street2>14677 COLLECTIONS CENTER DR</Street2><City>CHICAGO</City><State>IL</State><Zip>60693</Zip><ZipExtension>9999</ZipExtension></Address></BillingInfo><Signature><Indicator><True></True></Indicator></Signature></Practitioner><Facility><Name>TARGET OPTICAL</Name><Address><Street>1447 E SEVENTH ST</Street><City>MONTICELLO</City><State>MN</State><Zip>55362</Zip><ZipExtension>9999</ZipExtension></Address></Facility></HCFA1500></VSPClaimRequest>";
    public static final String SERVICE_CODE = "HCFA1500.ClaimLine.Procedure.HCPCCode";

    @Test
    public void getServiceCodes() {
        XMLParser parser = new XMLParser();
        List<String> values = parser.getValues(XML1, SERVICE_CODE);
        values.stream().forEach(System.out::println);
    }
}