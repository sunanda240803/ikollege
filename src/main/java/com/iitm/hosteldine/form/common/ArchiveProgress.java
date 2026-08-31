package com.iitm.hosteldine.form.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArchiveProgress {
    public LocalDateTime archiveId;
    int maxRecords;
    int progress;
    String progressPercent;
    String etc;
    String etl;
    String status;
    boolean isRunning = false;
    boolean stop = false;
    String lastLog = "";

    public void setProgress(int progress) {
        this.progress = progress;
        if (maxRecords > 0) {
            int prog = (int) (progress * 100.0 / maxRecords);
            progressPercent = prog + "%";
        }
    }

}
