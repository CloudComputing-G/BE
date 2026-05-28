package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.QuestionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionResultRepository extends JpaRepository<QuestionResult, Long> {

    @Query("""
            select qr from QuestionResult qr
            join fetch qr.question q
            where qr.submission.submissionId = :submissionId
            order by q.orderNum asc
            """)
    List<QuestionResult> findAllBySubmissionIdWithQuestion(@Param("submissionId") Long submissionId);

    @Query("""
            select qr from QuestionResult qr
            join fetch qr.question
            join fetch qr.submission s
            join fetch s.student
            join fetch s.assignment a
            join fetch a.teacher
            where s.submissionId = :submissionId
              and qr.question.questionId = :questionId
            """)
    Optional<QuestionResult> findBySubmissionIdAndQuestionId(
            @Param("submissionId") Long submissionId,
            @Param("questionId") Long questionId
    );

    @Query("""
            select qr from QuestionResult qr
            join fetch qr.question q
            join fetch qr.submission s
            join fetch s.student
            where s.assignment.assignmentId = :assignmentId
              and qr.regradeStatus = :regradeStatus
            order by s.submittedAt asc, q.orderNum asc
            """)
    List<QuestionResult> findByAssignmentIdAndRegradeStatus(
            @Param("assignmentId") Long assignmentId,
            @Param("regradeStatus") String regradeStatus
    );
}
