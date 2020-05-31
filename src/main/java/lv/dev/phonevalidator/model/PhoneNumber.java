package lv.dev.phonevalidator.model;

import lv.dev.phonevalidator.util.RestException;
import lv.dev.phonevalidator.service.PhoneNumberService;

public class PhoneNumber {

    private String rawData;
    private Region region;
    private String internalNumber;

    private PhoneNumber(String rawData, Region region, String internalNumber) {
        this.rawData = rawData;
        this.region = region;
        this.internalNumber = internalNumber;
    }

    public static PhoneNumber fromRawData(PhoneNumberService phoneNumberService, String rawData) {
        PhoneNumberService.RegionPhoneFormat regionPhoneFormat = phoneNumberService.getByRawNumber(rawData);
        if (regionPhoneFormat != null) {
            String regionalPhonePrefix = regionPhoneFormat.getCountryCode();
            Region region = new Region(regionalPhonePrefix, regionPhoneFormat.getRegionName());
            String internalNumberPart = rawData.substring(regionalPhonePrefix.length());
            return new PhoneNumber(rawData, region, internalNumberPart);
        }
        throw new RestException("Exception.countryNotFound");
    }

    public Region getRegion() {
        return region;
    }
}
