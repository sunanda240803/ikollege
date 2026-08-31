package com.iitm.hosteldine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "archive_table_master", schema = "archive")
public class ArchiveTableMasterEntity {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "table_name", nullable = false, length = Integer.MAX_VALUE)
    private String tableName;

    @NotNull
    @Column(name = "column_name", nullable = false, length = Integer.MAX_VALUE)
    private String columnName;

    @NotNull
    @ColumnDefault("'Y'")
    @Column(name = "active_flag", nullable = false, length = Integer.MAX_VALUE)
    private String activeFlag;

    @Column(name = "col_order")
    private Long colOrder;

}