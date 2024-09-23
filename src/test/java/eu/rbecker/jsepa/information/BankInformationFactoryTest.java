package eu.rbecker.jsepa.information;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Robert Becker <robert at rbecker.eu>
 */
public class BankInformationFactoryTest {

    public BankInformationFactoryTest() {
    }

    /**
     * Test of getCacheForIban method, of class BankInformationStore.
     */
    @Test
    public void testForIban() {
        assertTrue(BankInformationStore.getCacheForIban("DEXXXX").getCountryCode().equals("de"));
    }

    /**
     * Test of getCacheForCountryCode method, of class BankInformationStore.
     */
    @Test
    public void testForCountryCode() {
        assertTrue(BankInformationStore.getCacheForIban("de").getCountryCode().equals("de"));
        assertTrue(BankInformationStore.getCacheForIban("DE").getCountryCode().equals("de"));
    }


}
