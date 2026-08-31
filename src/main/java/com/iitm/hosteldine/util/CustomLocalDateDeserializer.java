package com.iitm.hosteldine.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {
    // Define the formatter to handle dates in "MMM dd, yyyy" format
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT, Locale.ENGLISH);

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = ((TextNode) p.getCodec().readTree(p)).textValue();
        return DateUtility.stringToLocalDate(dateString);
    }
}
