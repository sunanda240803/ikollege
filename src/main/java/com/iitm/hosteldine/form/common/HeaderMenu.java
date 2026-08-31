package com.iitm.hosteldine.form.common;

import lombok.Data;

@Data
public class HeaderMenu {
    private String menuHeading;
    private String menuUrl;

    public HeaderMenu setMenuHeading(String menuHeading) {
        this.menuHeading = menuHeading;
        return this;
    }

    public HeaderMenu setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
        return this;
    }
}
