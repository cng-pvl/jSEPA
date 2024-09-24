/*
 *  All rights reserved.
 */
package eu.rbecker.jsepa.directdebit.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.validator.routines.IBANValidator;

import eu.rbecker.jsepa.validation.BicValidator;

/**
 *
 * @author Robert Becker <robert at rbecker.eu>
 */
public class SepaUtil {

    private SepaUtil() {
        // ** Add a private constructor to hide the implicit public one
    }

    public static void validateIban(String iban) throws SepaValidationException {
        if (!IBANValidator.getInstance().isValid(iban)) {
            throw new SepaValidationException("Invalid IBAN " + iban);
        }
    }

    public static void validateBic(String bic) throws SepaValidationException {
        if (bic == null || bic.isEmpty() || !(new BicValidator().isValid(bic))) {
            throw new SepaValidationException("Invalid BIC " + bic);
        }
    }

    public static BigDecimal floatToBigInt2Digit(float f) {
        return BigDecimal.valueOf(f).setScale(2, RoundingMode.HALF_UP);
    }

}
