package lv.dev.phonevalidator.model;

public class Region {

    private String code;
    private String displayName;

    public Region(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
