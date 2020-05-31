package lv.dev.phonevalidator.service;

import org.springframework.lang.Nullable;

import java.util.regex.Pattern;

public interface PhoneNumberService {

    @Nullable
    RegionPhoneFormat getByRawNumber(String rawNumber);

    class RegionPhoneFormat {
        private String regionName;
        private String countryCode;
        @Nullable private Pattern secondaryCode;
        @Nullable private Pattern lengthRule;

        RegionPhoneFormat(String regionName, String countryCode, Pattern secondaryCode, Pattern lengthRule) {
            this.regionName = regionName;
            this.countryCode = countryCode;
            this.secondaryCode = secondaryCode;
            this.lengthRule = lengthRule;
        }

        public String getRegionName() {
            return regionName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        @Nullable Pattern getSecondaryCodePattern() {
            return secondaryCode;
        }

        @Nullable Pattern getLengthRule() {
            return lengthRule;
        }
    }
}
