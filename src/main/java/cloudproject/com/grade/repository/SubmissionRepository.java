package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // PENDING 이상(실제 제출된) 수
    long countByAssignment_AssignmentIdAndGradingStatusIn(Long assignmentId, java.util.List<String> statuses);

    // 과제별 특정 상태 수 (DONE = 채점완료)
    long countByAssignment_AssignmentIdAndGradingStatus(Long assignmentId, String gradingStatus);
    // confirm용
    Optional<Submission> findBySubmissionIdAndAssignment_AssignmentId(Long submissionId, Long assignmentId);

    // 중복 제출 체크
    Optional<Submission> findByAssignment_AssignmentIdAndStudent_UserId(Long assignmentId, Long studentId);
}
