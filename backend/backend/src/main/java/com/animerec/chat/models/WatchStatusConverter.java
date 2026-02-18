package com.animerec.chat.models;

import com.animerec.chat.enums.WatchStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WatchStatusConverter implements AttributeConverter<WatchStatus, String> {

    @Override
    public String convertToDatabaseColumn(WatchStatus attribute) {
        if (attribute == null) return null;
        return attribute.name().toLowerCase();
    }

    @Override
    public WatchStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return WatchStatus.valueOf(dbData.toUpperCase());
    }
}
