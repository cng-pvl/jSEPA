/*
 *  All rights reserved.
 */
package eu.rbecker.jsepa.directdebit;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import eu.rbecker.jsepa.directdebit.util.SepaXmlDocumentBuilder08;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.AccountIdentification4Choice;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.ActiveOrHistoricCurrencyAndAmount;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.CashAccount38;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.ChargeBearerType1Code;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.CustomerDirectDebitInitiationV08;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.DirectDebitTransaction10;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.DirectDebitTransactionInformation23;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.DocumentSDD;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.GenericPersonIdentification1;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.GroupHeader83;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.LocalInstrument2Choice;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.MandateRelatedInformation14;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.ObjectFactory;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.Party38Choice;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.PartyIdentification135;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.PaymentIdentification6;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.PaymentInstruction29;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.PaymentMethod2Code;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.PaymentTypeInformation29;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.PersonIdentification13;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.PersonIdentificationSchemeName1Choice;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.RemittanceInformation16;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.ServiceLevel8Choice;

/**
 *
 * @author Robert Becker <robert at rbecker.eu>
 */
public class DirectDebitDocumentBuilder08 extends SepaXmlDocumentBuilder08 {

    private static final long serialVersionUID = 1L;

    public static String toXml(DirectDebitDocumentData ddd) {
        // sepa xml document
        DocumentSDD doc = new DocumentSDD();
        // CustomerDirectDebitInitiationV02
        CustomerDirectDebitInitiationV08 cddiv = new CustomerDirectDebitInitiationV08();
        doc.setCstmrDrctDbtInitn(cddiv);

        // group header
        cddiv.setGrpHdr(createGroupHeaderSdd(ddd));

        cddiv.getPmtInf().addAll(createPaymentInstructions(ddd));

        // marshal to string
        StringWriter resultWriter = new StringWriter();
        marshal(doc.getClass().getPackage().getName(), new ObjectFactory().createDocument(doc), resultWriter);
        return resultWriter.toString();
    }

    private static List<PaymentInstruction29> createPaymentInstructions(DirectDebitDocumentData ddd) {
        return Arrays.stream(MandateType.values())
            .filter(mt -> ddd.getNumberOfPaymentsByMandateType(mt) > 0)
            .map(mt -> createPaymentInstructionInformation(ddd, mt))
            .collect(Collectors.toList());
    }

    private static PaymentInstruction29 createPaymentInstructionInformation(DirectDebitDocumentData ddd, MandateType mandateType) {

        PaymentInstruction29 result = new PaymentInstruction29();
        // payment information id
        result.setPmtInfId(ddd.getDocumentMessageId());
        // payment method (fixed)
        result.setPmtMtd(PaymentMethod2Code.DD);
        // batch booking (fixed)
        result.setBtchBookg(Boolean.TRUE);

        // number of transactions
        result.setNbOfTxs(String.valueOf(ddd.getNumberOfPaymentsByMandateType(mandateType)));
        // control sum
        result.setCtrlSum(ddd.getTotalPaymentSumOfPaymentsByMandateType(mandateType));
        // payment type information
        result.setPmtTpInf(createPaymentTypeInformation(mandateType));

        // requested collection due date
        result.setReqdColltnDt(dateToXmlGregorianCalendarDate(ddd.getDueDateByMandateType(mandateType)));

        // creditor name
        result.setCdtr(new PartyIdentification135());
        result.getCdtr().setNm(ddd.getCreditorName());

        // creditor iban
        result.setCdtrAcct(ibanToCashAccountSepa1(ddd.getCreditorIban()));

        // creditor agt(?)
        result.setCdtrAgt(bicToBranchAndFinancialInstitutionIdentification(ddd.getCreditorBic()));

        // whatever, fixed
        result.setChrgBr(ChargeBearerType1Code.SLEV);

        // single payment transactions ... yay!
        result.getDrctDbtTxInf().addAll(createDirectDebitTransactionInformationBlocks(ddd, mandateType));

        return result;
    }

    private static CashAccount38 ibanToCashAccountSepa1(String iban) {
        CashAccount38 result = new CashAccount38();
        result.setId(new AccountIdentification4Choice());
        result.getId().setIBAN(iban);
        return result;
    }

    private static Collection<DirectDebitTransactionInformation23> createDirectDebitTransactionInformationBlocks(DirectDebitDocumentData ddd, MandateType mandateType) {
        List<DirectDebitTransactionInformation23> result = new ArrayList<>();

        for (DirectDebitPayment p : ddd.getPaymentsByMandateType(mandateType)) {
            result.add(createDirectDebitTransaction(ddd, p));
        }

        return result;
    }

    private static DirectDebitTransactionInformation23 createDirectDebitTransaction(DirectDebitDocumentData ddd, DirectDebitPayment p) {
        DirectDebitTransactionInformation23 result = new DirectDebitTransactionInformation23();
        // mandate id
        result.setPmtId(new PaymentIdentification6());
        result.getPmtId().setEndToEndId(p.getMandateId());

        // currency and amount
        result.setInstdAmt(new ActiveOrHistoricCurrencyAndAmount());
        result.getInstdAmt().setCcy(p.getPaymentCurrency());
        result.getInstdAmt().setValue(p.getPaymentSum());

        // transaction information
        result.setDrctDbtTx(createDirectDebitTransaction(p, ddd));

        // debitor bic
        result.setDbtrAgt(bicToBranchAndFinancialInstitutionIdentification(p.getDebitorBic()));

        // debitor name
        result.setDbtr(new PartyIdentification135());
        result.getDbtr().setNm(p.getDebitorName());

        // debitor iban
        result.setDbtrAcct(new CashAccount38());
        result.getDbtrAcct().setId(new AccountIdentification4Choice());
        result.getDbtrAcct().getId().setIBAN(p.getDebitorIban());

        // reson of payment
        result.setRmtInf(new RemittanceInformation16());
        result.getRmtInf().getUstrd().add(p.getReasonForPayment());

        return result;
    }

    private static DirectDebitTransaction10 createDirectDebitTransaction(DirectDebitPayment p, DirectDebitDocumentData ddd) {
        DirectDebitTransaction10 result = new DirectDebitTransaction10();
        // mandate related info
        result.setMndtRltdInf(new MandateRelatedInformation14());

        // Erforderlich, wenn das Mandat seit letzten SEPA Lastschrift Einreichung ge�ndert wurde.
        // In diesem Fall ist das Feld mit "TRUE" zu belegen, ansonsten bleibt es leer.
        // Relevanz f�r folgende Mandats�nderungen: Gl�ubiger ID, Gl�ubiger Name, Bankverbindung des Zahlungspflichtigen, Mandat ID
        // -- we'll leave it empty for now and see what happens
        // tx.getMndtRltdInf().setAmdmntInd(Boolean.FALSE);
        result.getMndtRltdInf().setMndtId(p.getMandateId());
        result.getMndtRltdInf().setDtOfSgntr(dateToXmlGregorianCalendarDate(p.getMandateDate()));

        // creditor related info
        result.setCdtrSchmeId(new PartyIdentification135());
        result.getCdtrSchmeId().setId(new Party38Choice());
        result.getCdtrSchmeId().getId().setPrvtId(new PersonIdentification13());

        // person identification - (creditor identifier)
        GenericPersonIdentification1 inf = new GenericPersonIdentification1();
        result.getCdtrSchmeId().getId().getPrvtId().getOthr().add(inf);
        inf.setId(ddd.getCreditorIdentifier());

        // whatever, fixed to SEPA
        inf.setSchmeNm(new PersonIdentificationSchemeName1Choice());
        inf.getSchmeNm().setPrtry("SEPA");

        return result;
    }

    private static PaymentTypeInformation29 createPaymentTypeInformation(MandateType mandateType) {
        PaymentTypeInformation29 paymentType = new PaymentTypeInformation29();
        paymentType.getSvcLvl().add(new ServiceLevel8Choice());
        paymentType.getSvcLvl().get(0).setCd("SEPA");
        paymentType.setLclInstrm(new LocalInstrument2Choice());
        paymentType.getLclInstrm().setCd("CORE");
        paymentType.setSeqTp(mandateType.getSepaSequenceType3Code());
        return paymentType;
    }

    private static GroupHeader83 createGroupHeaderSdd(DirectDebitDocumentData ddd) {
        GroupHeader83 result = new GroupHeader83();
        // message id
        result.setMsgId(ddd.getDocumentMessageId());

        // created on
        result.setCreDtTm(calendarToXmlGregorianCalendarDateTime(Calendar.getInstance()));

        // number of tx
        result.setNbOfTxs(String.valueOf(ddd.getPayments().size()));

        // control sum
        result.setCtrlSum(ddd.getTotalPaymentSum());

        // creditor name
        PartyIdentification135 pi = new PartyIdentification135();
        pi.setNm(ddd.getCreditorName());

        result.setInitgPty(pi);

        return result;
    }

}
