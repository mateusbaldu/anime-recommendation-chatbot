package com.animerec.chat.models;

import com.animerec.chat.enums.WorkType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WorkTypeConverter implements AttributeConverter<WorkType, String> {

    @Override
    public String convertToDatabaseColumn(WorkType attribute) {
        if (attribute == null) return null;
        return attribute.name().toLowerCase();
    }

    @Override
    public WorkType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return WorkType.valueOf(dbData.toUpperCase());
    }
}
