package cloudproject.com.assignment.dto.response;

import cloudproject.com.assignment.domain.Assignment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AssignmentResponse {

    private Long assignmentId;
    private String title;
    private String subject;
    private String status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private List<QuestionResponse> questions;

    private long totalCount;       // 반 전체 학생 수
    private long submittedCount;   // 제출한 학생 수
    private long gradedCount;      // 채점 완료 수
    private long notSubmittedCount; // 미제출 수

    //Entity -> DTO
    public static AssignmentResponse from(Assignment assignment){
        AssignmentResponse dto = new AssignmentResponse();
        dto.assignmentId= assignment.getAssignmentId();
        dto.title = assignment.getTitle();
        dto.subject = assignment.getSubject();
        dto.status = assignment.getStatus();
        dto.dueDate = assignment.getDueDate();
        dto.createdAt = assignment.getCreatedAt();
        // questions 리스트도 변환
        dto.questions = assignment.getQuestions().stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
        return dto;
    }

    // 집계 포함 (목록 조회용)
    public static AssignmentResponse of(Assignment assignment,
                                        long totalCount,
                                        long submittedCount,
                                        long gradedCount) {
        AssignmentResponse dto = from(assignment);
        dto.totalCount = totalCount;
        dto.submittedCount = submittedCount;
        dto.gradedCount = gradedCount;
        dto.notSubmittedCount = totalCount - submittedCount;
        return dto;
    }
}
