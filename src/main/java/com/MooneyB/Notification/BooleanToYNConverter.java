package com.MooneyB.Notification;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) // 이 컨버터를 모든 boolean <-> CHAR(1) 매핑에 자동 적용
public class BooleanToYNConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        // Java의 boolean 값을 데이터베이스의 'Y' 또는 'N'으로 변환
        return (attribute != null && attribute) ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        // 데이터베이스의 'Y' 또는 'N' 값을 Java의 boolean 값으로 변환
        return "Y".equalsIgnoreCase(dbData);
    }
}
