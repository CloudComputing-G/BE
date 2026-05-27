package cloudproject.com.grade.dto.response;

import cloudproject.com.grade.domain.AnalyticsRecord;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AssignmentAnalyticsResponse {

    private Long analyticsId;
    private Long studentId;
    private String studentName;
    private String questionType;
    private Integer errorCount;
    private Integer totalCount;
    private Float predictedErrorRate;
    private LocalDateTime updatedAt;

    public static AssignmentAnalyticsResponse from(AnalyticsRecord record) {
        AssignmentAnalyticsResponse dto = new AssignmentAnalyticsResponse();
        dto.analyticsId = record.getAnalyticsId();
        dto.studentId = record.getStudent().getUserId();
        dto.studentName = record.getStudent().getName();
        dto.questionType = record.getQuestionType();
        dto.errorCount = record.getErrorCount();
        dto.totalCount = record.getTotalCount();
        dto.predictedErrorRate = record.getPredictedErrorRate();
        dto.updatedAt = record.getUpdatedAt();
        return dto;
    }
}
