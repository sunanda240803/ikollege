package com.iitm.hosteldine.repository.feedback;

import com.iitm.hosteldine.model.feedback.FeedbackQuestionEntity;
import com.iitm.hosteldine.service.reports.FeedbackRecord;
import com.iitm.hosteldine.service.reports.FeedbackWeightageViewRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FeedbackQuestionRepository extends JpaRepository<FeedbackQuestionEntity, Long> {
    Optional<FeedbackQuestionEntity> findByFeedbackQuesIdAndActiveFlag(Long id, String activeFlag);

    List<FeedbackQuestionEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from FeedbackQuestionEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

    List<FeedbackQuestionEntity> findAllByActiveFlag(String statusActive);

    Page<FeedbackQuestionEntity> findAllByActiveFlag(String statusActive, Pageable pageable);


    @Query("SELECT feed FROM FeedbackQuestionEntity feed WHERE feed.activeFlag = :statusActive AND (" +
            "feed.feedbackQuesDesc ILIKE CONCAT('%', :search, '%') OR " +
            "CAST(feed.feedbackWeightage AS string) ILIKE CONCAT('%', :search, '%'))")
    Page<FeedbackQuestionEntity> getFeedbackSearchList(String statusActive, Pageable pageable, String search);


    @Query(value = """
    select new com.iitm.hosteldine.service.reports.FeedbackRecord(count(feed.feedbackQuesDesc), sum(feed.feedbackWeightage)) 
        from FeedbackQuestionEntity feed where feed.activeFlag = :activeFlag
    """)
    Optional<FeedbackRecord> getFeedbackCountAndWeightageCount(String activeFlag);

    @Query(value = """
    select new com.iitm.hosteldine.service.reports.FeedbackWeightageViewRecord(cwfv.messMasterId,cwfv.messName,cwfv.studentCount,
        sum(cwfv.overallWeightage),cwfv.mmcDDiningFromdate,cwfv.mmcDDiningTodate)
        from CatererWiseFeedbackWeightageViewEntity cwfv
        where cwfv.mmcNId = :messPeriodId
            group by cwfv.messMasterId,cwfv.messName,cwfv.studentCount,cwfv.mmcDDiningFromdate,cwfv.mmcDDiningTodate
    """)
    List<FeedbackWeightageViewRecord>getFeedbackWeightageView(Long messPeriodId);

    @Query(value = """
    select smcf.messMasterId,mm.messName,smcf.feedbackQuesId,fqe.feedbackQuesDesc,fqe.feedbackWeightage,sum(smcf.feedbackScore),
        count(*) as student_count,cast((cast(sum(smcf.feedbackScore) as float)/ cast(count(distinct smcf.studentId) as float)) as double) as overall_weightage,
            mmc.diningFromDate,mmc.diningToDate
        from StudentMessCatererFeedbackEntity smcf join MessMasterControllerEntity  mmc on (smcf.messControllerId = mmc.id)
        join MessMasterEntity  mm on (smcf.messMasterId = mm.id) left join FeedbackQuestionEntity fqe on (fqe.feedbackQuesId = smcf.feedbackQuesId)
            where mmc.id = :messPeriodId group by smcf.messMasterId,fqe.feedbackQuesDesc,smcf.feedbackQuesId,mm.messName,fqe.feedbackWeightage,
                mmc.diningFromDate,mmc.diningToDate ORDER BY mm.messName ASC, smcf.feedbackQuesId
    """)
    List<Object[]> getFeedbackAndMessMasterAndMessCatererDetails(Long messPeriodId);
}