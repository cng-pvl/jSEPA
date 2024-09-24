/*
 *  All rights reserved.
 */
package eu.rbecker.jsepa.directdebit;

import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_02.SequenceType1Code;
import eu.rbecker.jsepa.directdebit.xml.schema.pain_008_001_08.SequenceType3Code;

/**
 *
 * @author Robert Becker <robert at rbecker.eu>
 */
public enum MandateType {

    ONE_OFF, RECURRENT, RECURRENT_FIRST, RECURRENT_FINAL;

    public SequenceType1Code getSepaSequenceType1Code() {
        switch (this) {
            case ONE_OFF:
                return SequenceType1Code.OOFF;
            case RECURRENT:
                return SequenceType1Code.RCUR;
            case RECURRENT_FINAL:
                return SequenceType1Code.FNAL;
            case RECURRENT_FIRST:
                return SequenceType1Code.FRST;
        }
        return null;
    }

    public SequenceType3Code getSepaSequenceType3Code() {
        switch (this) {
            case ONE_OFF:
                return SequenceType3Code.OOFF;
            case RECURRENT:
                return SequenceType3Code.RCUR;
            case RECURRENT_FINAL:
                return SequenceType3Code.FNAL;
            case RECURRENT_FIRST:
                return SequenceType3Code.FRST;
        }
        return null;
    }

}
