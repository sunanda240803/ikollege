package com.iitm.hosteldine.form.biometric;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
public class MessDetailsCountForm {
    private Long messId;
    private String messName;
    private Date messFrom;
    private Date messTo;
    private Integer totalStudentCount;
    private Integer totalPushedCount;
    private Integer noFacialData;
    private Integer pushedTodayCount;
    private Integer toBePushedTodayCount;
    private Integer totalToBePushedCount;
    private Integer toBeRemovedTodayCount;
    private Integer totalToBeRemovedCount;

    private ArrayList<MessDetailsCountForm> messList;
}
