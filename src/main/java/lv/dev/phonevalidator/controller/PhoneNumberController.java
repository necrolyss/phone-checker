package lv.dev.phonevalidator.controller;

import lv.dev.phonevalidator.model.PhoneNumber;
import lv.dev.phonevalidator.payloads.GetRegionByPhoneNumberRequest;
import lv.dev.phonevalidator.payloads.GetRegionByPhoneNumberResponse;
import lv.dev.phonevalidator.service.PhoneNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/phone")
public class PhoneNumberController {

    @Autowired
    private PhoneNumberService phoneNumberService;

    @PostMapping("/countryByNumber")
    public GetRegionByPhoneNumberResponse getCountryByPhoneNumber(@Valid @RequestBody GetRegionByPhoneNumberRequest getRegionByPhoneNumberRequest) {
        PhoneNumber phoneNumber = PhoneNumber.fromRawData(phoneNumberService, getRegionByPhoneNumberRequest.getRawNumber());
        return new GetRegionByPhoneNumberResponse(phoneNumber.getRegion().getDisplayName());
    }

}
