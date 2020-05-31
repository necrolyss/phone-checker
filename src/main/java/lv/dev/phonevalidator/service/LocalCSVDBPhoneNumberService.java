package lv.dev.phonevalidator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LocalCSVDBPhoneNumberService implements PhoneNumberService {

    @Value("${code.local.csvdb.location}")
    private String localDbLocation;

    @Value("${code.local.csvdb.delimiter}")
    private String localDbEntityDelimiter;

    private List<RegionPhoneFormat> regionPhoneFormats;

    @PostConstruct
    private void loadPhonePatterns() {
        try {
            Path dbPath = Paths.get(localDbLocation);
            regionPhoneFormats = Files.lines(dbPath)
                    .map(s -> {
                        String[] parts = s.split(localDbEntityDelimiter, -1);

                        String territorialScope = parts[0];
                        String countryCode = parts[1];
                        Pattern additionalCodePattern = Pattern.compile(parts[2] + ".*$");
                        Pattern lengthRulePattern = extractInternalPhoneLengthPattern(parts[3]);

                        return new RegionPhoneFormat(
                                territorialScope,
                                countryCode,
                                additionalCodePattern,
                                lengthRulePattern
                        );
                    }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load phone number pattern database", e);
        }
    }

    private Pattern extractInternalPhoneLengthPattern(String phoneNumberLengthRule) {
        if (StringUtils.isEmpty(phoneNumberLengthRule)) {
            return null;
        }

        StringBuilder phoneNumberLengthPatternBuilder = new StringBuilder();
        if (phoneNumberLengthRule.contains("-")) {
            String[] boundaries = phoneNumberLengthRule.split("-");
            phoneNumberLengthPatternBuilder.append("^\\d{").append(boundaries[0]).append(",").append(boundaries[1]).append("}$");
        } else if (phoneNumberLengthRule.contains(",")) {
            String[] variants = phoneNumberLengthRule.split(",");
            for (int i = 0; i < variants.length; i++) {
                String variant = variants[i];
                phoneNumberLengthPatternBuilder.append("^\\d{").append(variant).append("}$");
                if (i < variants.length - 1) {
                    phoneNumberLengthPatternBuilder.append("|");
                }
            }
        } else {
            phoneNumberLengthPatternBuilder.append("^\\d{").append(phoneNumberLengthRule).append("}$");
        }
        return Pattern.compile(phoneNumberLengthPatternBuilder.toString());
    }

    List<RegionPhoneFormat> getRegionPhoneFormats() {
        return regionPhoneFormats;
    }


    @Override
    @Nullable
    public RegionPhoneFormat getByRawNumber(String rawNumber) {
        List<RegionPhoneFormat> candidatesByCountryCode = regionPhoneFormats.stream()
                .filter(regionPhoneFormat -> rawNumber.startsWith(regionPhoneFormat.getCountryCode()))
                .collect(Collectors.toList());
        if (candidatesByCountryCode.size() == 1) {
            return checkForInternalPartLength(rawNumber, candidatesByCountryCode.get(0));
        } else {
            if (candidatesByCountryCode.size() > 1) {
                int countryCodeLength = candidatesByCountryCode.get(0).getCountryCode().length();
                String numberWithoutCountryCode = rawNumber.substring(countryCodeLength);
                for (RegionPhoneFormat candidatePattern : candidatesByCountryCode) {
                    Pattern secondaryCodePattern = candidatePattern.getSecondaryCodePattern();
                    if (secondaryCodePattern != null) {
                        if (secondaryCodePattern.matcher(numberWithoutCountryCode).matches()) {
                            return checkForInternalPartLength(rawNumber, candidatePattern);
                        }
                    } else {
                        throw new RuntimeException("Secondary code missing for country code [" + numberWithoutCountryCode + "], please check DB");
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    private RegionPhoneFormat checkForInternalPartLength(String rawNumber, RegionPhoneFormat resolvedPattern) {
        int countryCodeLength = resolvedPattern.getCountryCode().length();
        Pattern lengthRule = resolvedPattern.getLengthRule();
        if (lengthRule == null || lengthRule.matcher(rawNumber.substring(countryCodeLength)).matches()) {
            return resolvedPattern;
        } else {
            return null;
        }
    }
}
