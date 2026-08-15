package com.manruhomerun.yadan.user.domain.converter;

import com.manruhomerun.yadan.travelspot.domain.enums.PreferredTravelRegionCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PreferredTravelRegionCodeConverter
        implements AttributeConverter<PreferredTravelRegionCode, String> {

    @Override
    public String convertToDatabaseColumn(PreferredTravelRegionCode attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public PreferredTravelRegionCode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PreferredTravelRegionCode.fromCode(dbData);
    }
}
