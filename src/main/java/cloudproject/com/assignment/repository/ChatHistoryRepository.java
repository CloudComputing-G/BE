package cloudproject.com.assignment.repository;

import cloudproject.com.assignment.domain.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    List<ChatHistory> findByStudent_UserIdAndQuestion_QuestionIdOrderByCreatedAtAsc(
            Long studentId, Long questionId);
}
