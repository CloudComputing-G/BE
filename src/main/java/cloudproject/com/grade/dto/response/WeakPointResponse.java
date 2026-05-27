package cloudproject.com.grade.dto.response;

import cloudproject.com.grade.domain.AnalyticsRecord;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WeakPointResponse {

    private Long analyticsId;
    private String questionType;
    private Integer errorCount;
    private Integer totalCount;
    private Float predictedErrorRate;
    private LocalDateTime updatedAt;

    private Long assignmentId;
    private String assignmentTitle;

    public static WeakPointResponse from(AnalyticsRecord record) {
        WeakPointResponse dto = new WeakPointResponse();
        dto.analyticsId = record.getAnalyticsId();
        dto.questionType = record.getQuestionType();
        dto.errorCount = record.getErrorCount();
        dto.totalCount = record.getTotalCount();
        dto.predictedErrorRate = record.getPredictedErrorRate();
        dto.updatedAt = record.getUpdatedAt();
        dto.assignmentId = record.getAssignment().getAssignmentId();
        dto.assignmentTitle = record.getAssignment().getTitle();
        return dto;
    }
}
