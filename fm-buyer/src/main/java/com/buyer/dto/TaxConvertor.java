package com.buyer.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TaxConvertor implements AttributeConverter<TaxDTO, String> {
    
    private static final Logger logger = LoggerFactory.getLogger(TaxConvertor.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(TaxDTO taxDTO) {
        try {
            return taxDTO != null ? objectMapper.writeValueAsString(taxDTO) : null;
        } catch (JsonProcessingException e) {
            logger.error("Error converting TaxDTO to JSON", e);
            return null;
        }
    }
    
    @Override
    public TaxDTO convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? objectMapper.readValue(dbData, TaxDTO.class) : null;
        } catch (JsonProcessingException e) {
            logger.error("Error converting JSON to TaxDTO", e);
            return null;
        }
    }
}