/*
 *  All rights reserved.
 */
package eu.rbecker.jsepa.directdebit.util;

/**
 *
 * @author Robert Becker <robert at rbecker.eu>
 */
public class SepaValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public SepaValidationException(String message) {
        super(message);
    }

}
