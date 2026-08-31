package com.iitm.hosteldine.util;

import java.lang.reflect.Field;

import org.springframework.stereotype.Component;

@Component
public class PropertyAccessorUtil {

	public String getPropertyValue(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(object);
            return value != null ? value.toString() : "";
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return "";
        }
    }
}
