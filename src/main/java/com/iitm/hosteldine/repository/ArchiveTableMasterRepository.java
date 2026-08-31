package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.ArchiveTableMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ArchiveTableMasterRepository extends JpaRepository<ArchiveTableMasterEntity, Long> {
    @Query(value = "select * from archive.create_archive_tables();", nativeQuery = true)
    void createArchiveTables();

    @Query(value = """
            select count(distinct(student_id)) from schooldev."SETTLEMENT_HISTORY" where active_flag = 'Y';
            """, nativeQuery = true)
    int getSettledStudentCount();

    @Query(value = """
            select count(distinct(settlement_id)) from schooldev."SETTLEMENT_HISTORY" where active_flag = 'Y';
            """, nativeQuery = true)
    Integer getSettlementCount();


    @Query(nativeQuery = true, value = """
            select * from archive.archive_settled_students_by_batch(:archiveId, :totalCount, :batch);
            """)
    void archiveByLimitAndBatch(LocalDateTime archiveId, int totalCount, String batch);

    @Query(nativeQuery = true, value = """
            select count(1) from (select distinct(substring(student_id from 3 for 2)) 
                                  from schooldev."SETTLEMENT_HISTORY" 
                                  where active_flag  = 'Y' 
                                  group by substring(student_id from 3 for 2)
                                  ) b;
            """)
    int getNoOfBatches();

    @Query(nativeQuery = true, value = """
            select substring(student_id from 3 for 2) as batch, cast(count(1) as varchar) as cnt
            from schooldev."SETTLEMENT_HISTORY"
            where active_flag  = 'Y'
            group by substring(student_id from 3 for 2)
            order by 1
            """)
    List<String[]> getBatchList();
}