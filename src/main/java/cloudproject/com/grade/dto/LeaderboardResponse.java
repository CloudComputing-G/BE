package cloudproject.com.grade.dto;

import cloudproject.com.grade.domain.GradingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

public record LeaderboardResponse(
        Long assignmentId,
        String assignmentTitle,
        Integer maxScore,
        List<RankingItem> rankings
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RankingItem(
            Integer rank,
            Long studentId,
            String studentName,
            Integer totalScore,
            Double correctRate,
            GradingStatus gradingStatus,
            LocalDateTime submittedAt
    ) {
    }
}
