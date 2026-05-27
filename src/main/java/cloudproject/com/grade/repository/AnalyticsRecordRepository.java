package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.AnalyticsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnalyticsRecordRepository extends JpaRepository<AnalyticsRecord, Long> {

    @Query("SELECT ar FROM AnalyticsRecord ar " +
           "JOIN FETCH ar.assignment a " +
           "WHERE ar.student.userId = :studentId " +
           "ORDER BY ar.predictedErrorRate DESC")
    List<AnalyticsRecord> findByStudentIdOrderByPredictedErrorRateDesc(@Param("studentId") Long studentId);

    @Query("SELECT ar FROM AnalyticsRecord ar " +
           "JOIN FETCH ar.student s " +
           "WHERE ar.assignment.assignmentId = :assignmentId " +
           "ORDER BY s.name ASC, ar.predictedErrorRate DESC")
    List<AnalyticsRecord> findByAssignmentIdWithStudent(@Param("assignmentId") Long assignmentId);
}
