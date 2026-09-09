package com.wayfarer.wayfarer_backend.model.converter;

import com.wayfarer.wayfarer_backend.service.auth_service.EmailCryptoService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EmailEncryptionConverter implements AttributeConverter<String, String> {

    private final EmailCryptoService cryptoService;

    public EmailEncryptionConverter(EmailCryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : cryptoService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : cryptoService.decrypt(dbData);
    }
}