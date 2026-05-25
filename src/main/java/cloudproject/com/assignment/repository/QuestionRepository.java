package cloudproject.com.assignment.repository;

import cloudproject.com.assignment.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    // 해당 과제의 문항인지 확인
    Optional<Question> findByQuestionIdAndAssignment_AssignmentId(Long questionId, Long assignmentId);
}
