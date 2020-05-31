package lv.dev.phonevalidator.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LocalCSVDBPhoneNumberServiceTest {

    @Autowired
    @Qualifier("localCSVDBPhoneNumberService")
    private LocalCSVDBPhoneNumberService localCSVDBPhoneNumberService;

    @Test
    public void testDatabaseIsCorrect() throws IOException {
        Path dbPath = Paths.get("./db/dial-codes.csv");
        int expectedPatternCount = Files.readAllLines(dbPath).size();
        int actualPatternCount = localCSVDBPhoneNumberService.getRegionPhoneFormats().size();
        assertEquals(expectedPatternCount, actualPatternCount, "Loaded phone number pattern rule size mismatch");
    }

    @Test
    public void testSinglePrefixInCorrectNumber() {
        PhoneNumberService.RegionPhoneFormat regionPhoneFormat = localCSVDBPhoneNumberService.getByRawNumber("28525654445");
        assertNull(regionPhoneFormat, "Incorrect single prefix phone number country was found");
    }

    @Test
    public void testSinglePrefixCorrectNumber() {
        PhoneNumberService.RegionPhoneFormat regionPhoneFormat = localCSVDBPhoneNumberService.getByRawNumber("37125654445");
        assertNotNull(regionPhoneFormat, "Single prefix phone number country was not found");
        assertEquals("Latvia", regionPhoneFormat.getRegionName(), "Found region name mismatch");
    }

    @Test
    public void testSecondaryPrefixNumbers() {
        assertCombedZoneNumbersResolveCorrectly("79095912514", "Russia");
        assertCombedZoneNumbersResolveCorrectly("76125912514", "Kazakhstan");
        assertCombedZoneNumbersResolveCorrectly("12425912514", "Bahamas,The");
        assertCombedZoneNumbersResolveCorrectly("18765912514", "Jamaica");
        assertCombedZoneNumbersResolveCorrectly("13335912514", "United States");
    }

    private void assertCombedZoneNumbersResolveCorrectly(String rawNumber, String expectedRegion) {
        PhoneNumberService.RegionPhoneFormat regionPhoneFormat = localCSVDBPhoneNumberService.getByRawNumber(rawNumber);
        assertNotNull(regionPhoneFormat, "Multiple prefix phone number country was not found");
        assertEquals(expectedRegion, regionPhoneFormat.getRegionName(), "Found region name mismatch");
    }

    @Test
    public void testPhoneNumberLength() {
        assertDifferentLengthNumbersResolvesCorrectly("423111111", false);
        assertDifferentLengthNumbersResolvesCorrectly("4231111111", true);
        assertDifferentLengthNumbersResolvesCorrectly("42311111111", true);
        assertDifferentLengthNumbersResolvesCorrectly("423111111111", true);
        assertDifferentLengthNumbersResolvesCorrectly("4231111111111", false);
    }

    private void assertDifferentLengthNumbersResolvesCorrectly(String rawNumber, boolean correct) {
        PhoneNumberService.RegionPhoneFormat regionPhoneFormat = localCSVDBPhoneNumberService.getByRawNumber(rawNumber);
        boolean comparisonResult = (correct == (regionPhoneFormat != null));
        assertTrue(comparisonResult, "Provided number length mismatched length rule");

    }

}
