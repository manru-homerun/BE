package com.manruhomerun.yadan.user.domain.converter;

import com.manruhomerun.yadan.travelspot.domain.enums.TravelRegionCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TravelRegionCodeConverter implements AttributeConverter<TravelRegionCode, String> {

    @Override
    public String convertToDatabaseColumn(TravelRegionCode attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public TravelRegionCode convertToEntityAttribute(String dbData) {
        return dbData == null ? null : TravelRegionCode.fromCode(dbData);
    }
}
