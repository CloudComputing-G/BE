package cloudproject.com.grade.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GradingResultRequest(
        String status,
        Integer totalScore,
        LocalDateTime gradedAt,
        String failReason,
        List<QuestionResultItem> questions
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record QuestionResultItem(
            Long questionId,
            Integer score,
            String reason,
            String imageUrl
    ) {
    }
}
