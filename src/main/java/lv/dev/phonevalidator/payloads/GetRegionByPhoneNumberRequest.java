package lv.dev.phonevalidator.payloads;

import javax.validation.constraints.Pattern;

public class GetRegionByPhoneNumberRequest {

    @Pattern(regexp="[\\d]{6,15}", message = "Validation.notAPhoneNumber")
    private String rawNumber;

    public String getRawNumber() {
        return rawNumber;
    }

    public void setRawNumber(String rawNumber) {
        this.rawNumber = rawNumber;
    }
}
