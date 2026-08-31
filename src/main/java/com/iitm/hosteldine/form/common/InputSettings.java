package com.iitm.hosteldine.form.common;

import lombok.Data;

@Data
public class InputSettings {
    private boolean needed = true;
    private boolean enabled = true;
    private boolean required = true;
    private String value = "";

    public InputSettings setNeeded(boolean needed) {
        this.needed = needed;
        return this;
    }

    public InputSettings setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public InputSettings setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public InputSettings setValue(String value) {
        this.value = value;
        return this;
    }

    public String getValue(String defaultValue) {
        if ((this.value == null || this.value.isEmpty()) && defaultValue != null) {
            return defaultValue;
        }
        return this.value;
    }

    @Override
    public String toString() {
        return "(" + needed + ", " + enabled + ", " + required + ", " + value + ')';
    }
}
