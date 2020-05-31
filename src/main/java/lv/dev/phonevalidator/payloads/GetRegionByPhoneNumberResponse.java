package lv.dev.phonevalidator.payloads;

public class GetRegionByPhoneNumberResponse {

    private String regionDisplayName;

    public GetRegionByPhoneNumberResponse(String regionDisplayName) {
        this.regionDisplayName = regionDisplayName;
    }

    public String getRegionDisplayName() {
        return regionDisplayName;
    }

    public void setRegionDisplayName(String regionDisplayName) {
        this.regionDisplayName = regionDisplayName;
    }
}
