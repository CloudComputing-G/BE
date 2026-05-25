package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.Submission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
            join fetch s.student
            where s.assignment.assignmentId = :assignmentId
            order by s.submittedAt desc
            """)
    List<Submission> findAllByAssignmentIdWithStudent(@Param("assignmentId") Long assignmentId);
}
