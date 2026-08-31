package com.iitm.hosteldine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "scheduler_logs", schema = "schooldev")
@NoArgsConstructor
public class SchedulerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "created_at")
    private LocalDateTime createdAt =  LocalDateTime.now();


    @Column
    private String scheduler;

    @Column
    private String log;


    public SchedulerLog(String tag, String logMsg) {
        this.log = logMsg;
        this.scheduler = tag;
    }
}