package com.example.demo.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute == null ? List.of() : attribute);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Gagal menyimpan daftar saran.", exception);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return List.of();
        }

        try {
            return OBJECT_MAPPER.readValue(dbData, STRING_LIST_TYPE);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Gagal membaca daftar saran.", exception);
        }
    }
}
