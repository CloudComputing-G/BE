package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {  // 과제별 전체 제출 수
    long countByAssignment_AssignmentId(Long assignmentId);

    // 과제별 특정 상태 수 (DONE = 채점완료)
    long countByAssignment_AssignmentIdAndGradingStatus(Long assignmentId, String gradingStatus);

    // confirm용
    Optional<Submission> findBySubmissionIdAndAssignment_AssignmentId(Long submissionId, Long assignmentId);
}
