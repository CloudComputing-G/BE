package cloudproject.com.assignment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AssignmentCreateRequest {
    @NotBlank(message = "제목은 필수입니다")
    private String title;

    private String subject;

    @NotNull(message = "반 ID는 필수입니다")
    private Long classId;

    private LocalDateTime dueDate;

    private String problemS3Key; // 교사 문제지 S3 키

    private String answerS3Key;  // 답지 S3 키

    @Valid // 리스트 안 객체도 검증
    private List<QuestionRequest> questions;

    // 문항 정보 (AssignmentCreateRequest 안에 중첩)
    @Getter
    public static class QuestionRequest {

        @NotBlank(message = "문항 내용은 필수입니다")
        private String content;

        private String answer;

        private String gradingCriteria;

        private Integer maxScore;

        private Integer orderNum;
    }
}
