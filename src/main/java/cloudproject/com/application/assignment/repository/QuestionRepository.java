package cloudproject.com.application.assignment.repository;

import cloudproject.com.application.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    // 해당 과제의 문항인지 확인
    Optional<Question> findByQuestionIdAndAssignment_AssignmentId(Long questionId, Long assignmentId);
}
