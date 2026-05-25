package cloudproject.com.assignment.repository;

import cloudproject.com.assignment.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("select coalesce(sum(q.maxScore), 0) from Question q where q.assignment.assignmentId = :assignmentId")
    Long sumMaxScoreByAssignmentId(@Param("assignmentId") Long assignmentId);
}
