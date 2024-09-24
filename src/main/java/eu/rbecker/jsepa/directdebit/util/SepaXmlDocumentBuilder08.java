package eu.rbecker.jsepa.directdebit.util;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.BranchAndFinancialInstitutionIdentification6;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.FinancialInstitutionIdentification18;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 *
 * @author Robert Becker <robert at rbecker.eu>
 */
public abstract class SepaXmlDocumentBuilder08 implements Serializable {

    protected static final long serialVersionUID = 1L;

    /**
     * Returns a XMLGregorianCalendar in the format of 2014-01-22T17:53:01
     *
     * @param cal
     * @return
     * @throws DatatypeConfigurationException
     */
    protected static XMLGregorianCalendar calendarToXmlGregorianCalendarDateTime(Calendar cal) {
        // this way of initialization is required to omit time zone and milli seconds -.-
        XMLGregorianCalendar result;
        try {
            result = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        result.setYear(cal.get(Calendar.YEAR));
        result.setMonth(cal.get(Calendar.MONTH) + 1);
        result.setDay(cal.get(Calendar.DAY_OF_MONTH));
        result.setHour(cal.get(Calendar.HOUR_OF_DAY));
        result.setMinute(cal.get(Calendar.MINUTE));
        result.setSecond(cal.get(Calendar.SECOND));
        return result;
    }

    protected static XMLGregorianCalendar dateToXmlGregorianCalendarDateTime(Date d) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        return calendarToXmlGregorianCalendarDateTime(cal);
    }

    protected static XMLGregorianCalendar dateToXmlGregorianCalendarDate(Date d) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        return calendarToXmlGregorianCalendarDate(cal);
    }

    protected static XMLGregorianCalendar calendarToXmlGregorianCalendarDate(GregorianCalendar d) {
        XMLGregorianCalendar result;
        try {
            result = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(d.get(Calendar.YEAR), d.get(Calendar.MONTH) + 1, d.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    protected static void marshal(String contextName, JAXBElement jaxbDoc, StringWriter resultWriter) {
        try {
        JAXBContext jaxbCtx = JAXBContext.newInstance(contextName);
        Marshaller marshaller = jaxbCtx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(jaxbDoc, resultWriter);
        } catch (JAXBException e) {
            // If something crashes here it needs to be fixed in the library, not by the user.
            throw new RuntimeException(e);
        }
    }

    protected static BranchAndFinancialInstitutionIdentification6 bicToBranchAndFinancialInstitutionIdentification(String bic) {
        BranchAndFinancialInstitutionIdentification6 result = new BranchAndFinancialInstitutionIdentification6();
        result.setFinInstnId(new FinancialInstitutionIdentification18());
        result.getFinInstnId().setBICFI(bic);
        return result;
    }

}
