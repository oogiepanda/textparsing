package org.example;

import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;

/**
 * Utility to get/set xml values
 */
public class XmlScraper {

    public static Pair createBeginEndElement(String qualifiedName) {
        String[] names = qualifiedName.split("\\.");
        StringBuffer key = new StringBuffer();
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < names.length; i++) {
            key.append("<").append(names[i]).append(">");
            if (i != names.length - 1) {
                key.append(" ");
            }
        }
        value.append("</").append(names[names.length - 1]).append(">");
        return new Pair(key.toString(), value.toString());
    }

    /**
     * @param elementStart "<Patient> <IndividualName> <First>"
     * @param elementEnd   "</First>"
     * @param xml
     * @return May return an empty string but never a null
     */
    public String getElementValue(String xml, String elementStart, String elementEnd) {
        String[] elements = elementStart.split(" ");
        for (int i = 0; i < elements.length; i++) {
            int start = xml.indexOf(elements[i]);
            if (start > 0) {
                xml = xml.substring(start);
            }
        }
        int end = xml.indexOf(elementEnd);
        if (end != -1) {
            return xml.substring(elements[elements.length - 1].length(), end);
        }
        return "";
    }

    /**
     * @param xml
     * @param qualifiedNames {"ClaimLine.ServiceDate.FromDate", "ClaimLine.ServiceDate.ToDate" }
     * @param newValue
     * @return Newly updated xml string
     */
    public String setValues(String xml, List<String> qualifiedNames, String newValue) {
        for (int i = 0; i < qualifiedNames.size(); i++) {
            xml = setValue(xml, qualifiedNames.get(i), newValue);
        }
        return xml;
    }

    /**
     * @param xml
     * @param qualifiedName "HCFA1500.SpecificReserved.BenefitRequestNumber"
     * @param newValue
     * @return Newly updated xml string
     */
    public String setValue(String xml, String qualifiedName, String newValue) {
        if (getValues(xml, qualifiedName).isEmpty()) {
            return xml;
        }
        int parentStartIndex = 0;
        int parentEndIndex = xml.length() - 1;
        int startIndex = 0;

        for (String name : qualifiedName.split("\\.")) {
            String beginElement = String.format("<%s", name);
            startIndex = indexOf(xml, beginElement, parentStartIndex, parentEndIndex);
            if (startIndex == -1) {
                return xml;
            }
            parentStartIndex = startIndex;
            parentEndIndex = xml.indexOf(String.format("</%s", name), parentStartIndex);
        }
        return createNewXml(xml, newValue, startIndex, parentEndIndex);
    }

    /**
     * Method to ensure that search for non unique Xml Elements ie <DateOfBirth> are within the expected indices.
     *
     * @param text
     * @param searchWord
     * @param start
     * @param end
     * @return -1 for no match
     */
    private int indexOf(String text, String searchWord, int start, int end) {
        int index = start;
        while (true) {
            index = text.indexOf(searchWord, index);
            if (index == -1 || index < end) {
                return index;
            }
        }
    }

    private String createNewXml(String xml, String newValue, int start, int end) {
        String s1 = xml.substring(0, xml.indexOf('>', start) + 1);
        String s2 = xml.substring(end);
        StringBuffer nuXml = new StringBuffer();
        nuXml.append(s1).append(newValue).append(s2).toString();
        return nuXml.toString();
    }

    /**
     * @param xml
     * @param qualifiedName "HCFA1500.SpecificReserved.BenefitRequestNumber"
     * @return May return an empty string but never a null
     */
    public String getValue(String xml, String qualifiedName) {
        List<String> values = getValues(xml, qualifiedName);
        return values.isEmpty() ? "" : values.get(0);
    }

    /**
     * @param xml
     * @param qualifiedNames List of elements {"HCFA1500.SpecificReserved.BenefitRequestNumber", "BatchLocator"}
     * @return May return an empty list but never a null
     */
    public List<String> getValues(String xml, List<String> qualifiedNames) {
        List<String> values = new ArrayList<>();
        qualifiedNames.forEach(n -> {
            List<String> vals = getValues(xml, n);
            values.add(vals.isEmpty() ? "" : vals.get(0));
        });
        return values;
    }

    /**
     * <BasicForm>
     * <Option ProvidedBy="L" OptSvcCd="A88">QV</Option>
     * <Option ProvidedBy="L">AD</Option>
     * </BasicForm>
     *
     * @param xml
     * @param qualifiedName "HCFA1500.SpecificReserved.BenefitRequestNumber"
     * @return May return an Empty List but never a null
     */
    public List<String> getValues(String xml, String qualifiedName) {
        String[] names = qualifiedName.split("\\.");

        if (names.length == 0) {
            return Collections.EMPTY_LIST;
        }
        final String[] xmls = new String[]{xml};

        stream(names).forEach(n -> xmls[0] = extractXmlSubString(xmls[0], n));
        String lastElement = names[names.length - 1];
        List<String> values = new ArrayList<>();

        while (true) {
            Optional<String> value = getValue(xmls, lastElement);
            if (!value.isPresent()) {
                break;
            }
            ;
            value.ifPresent(values::add);
        }
        return values;
    }

    private String extractXmlSubString(String xml, String name) {
        int start = getStartIndexFromBeginOfXml(xml, name);
        int end = getEndIndexFromBackOfXml(xml, name);

        return (start == -1 || end == -1) ? xml : xml.substring(start, end);
    }

    private int getStartIndexFromBeginOfXml(String xml, String name) {
        String element = String.format("<%s", name);
        int index = xml.indexOf(element);
        return (index < 0) ? -1 : index;
    }

    private int getEndIndexFromBackOfXml(String xml, String name) {
        String element = String.format("</%s>", name);
        int index = xml.lastIndexOf(element);
        return (index < 0) ? -1 : (index + element.length());
    }

    private Optional<String> getValue(String[] xmls, String name) {
        if (StringUtils.isBlank(xmls[0]) || StringUtils.isBlank(name)) {
            return Optional.empty();
        }
        String xml = xmls[0];
        int start = getStartIndexFromBeginOfXml(xml, name);
        int end = xml.indexOf(String.format("</%s>", name));

        if (start == -1 || end == -1) {
            return Optional.empty();
        }
        start = xml.indexOf('>');

        xmls[0] = xml.substring(end + 1 + String.format("<%s>", name).length());
        return Optional.of(xml.substring(start + 1, end).trim());
    }
}
