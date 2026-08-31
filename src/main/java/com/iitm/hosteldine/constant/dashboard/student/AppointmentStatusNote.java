package com.iitm.hosteldine.constant.dashboard.student;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum AppointmentStatusNote {
    SCHOLARS_STAY_EXTENSION_VALIDATING_NOTE("message.label.scholar.stay.extension.validating.note"),
    SCHOLARS_STAY_EXTENSION_CANCEL_NOTE("message.label.scholar.stay.extension.cancel.note"),
    SCHOLARS_STAY_EXTENSION_PRE_CANCEL_NOTE("message.label.scholar.stay.extension.pre.cancel.note"),
    SCHOLARS_STAY_EXTENSION_APPROVED_CANCEL_NOTE("message.label.scholar.stay.extension.approved.cancel.note");
    private final String noteKey;
    AppointmentStatusNote(String noteKey) {
        this.noteKey = noteKey;
    }
}
