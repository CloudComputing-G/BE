package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.Submission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("""
            select s from Submission s
            join fetch s.assignment a
            join fetch a.teacher
            join fetch s.student
            where s.submissionId = :submissionId
            """)
    Optional<Submission> findDetailById(@Param("submissionId") Long submissionId);

    @Query("""
            select s from Submission s
            join fetch s.assignment a
            join fetch a.teacher
            join fetch s.student
            where a.assignmentId = :assignmentId
              and s.student.userId = :studentId
            order by s.submittedAt desc
            """)
    List<Submission> findLatestByAssignmentIdAndStudentId(
            @Param("assignmentId") Long assignmentId,
            @Param("studentId") Long studentId,
            Pageable pageable
    );

    @Query("""
            select s from Submission s
            join fetch s.assignment a
            join fetch a.teacher
            join fetch s.student
            where a.assignmentId = :assignmentId
            order by s.submittedAt desc
            """)
    List<Submission> findAllByAssignmentIdWithStudent(@Param("assignmentId") Long assignmentId);

    // PENDING 이상(실제 제출된) 수
    long countByAssignment_AssignmentIdAndGradingStatusIn(Long assignmentId, List<String> statuses);

    // 과제별 특정 상태 수 (DONE = 채점완료)
    long countByAssignment_AssignmentIdAndGradingStatus(Long assignmentId, String gradingStatus);

    // confirm용
    Optional<Submission> findBySubmissionIdAndAssignment_AssignmentId(Long submissionId, Long assignmentId);

    // 중복 제출 체크
    Optional<Submission> findByAssignment_AssignmentIdAndStudent_UserId(Long assignmentId, Long studentId);

    @Query("""
            select s from Submission s
            join fetch s.assignment a
            where s.student.userId = :studentId
            order by s.submittedAt desc
            """)
    List<Submission> findAllByStudentId(@Param("studentId") Long studentId);

    @Modifying
    @Query("update Submission s set s.gradingStatus = 'FAILED', s.failReason = :reason, s.gradedAt = current_timestamp where s.gradingStatus = 'PENDING'")
    int failAllPending(@Param("reason") String reason);
}
