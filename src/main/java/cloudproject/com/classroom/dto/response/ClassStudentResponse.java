package cloudproject.com.classroom.dto.response;

import cloudproject.com.classroom.domain.ClassStudent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "반 학생 응답")
@Getter
public class ClassStudentResponse {

    @Schema(description = "학생 ID", example = "3")
    private Long studentId;

    @Schema(description = "학생 이름", example = "홍길동")
    private String studentName;

    @Schema(description = "학생 이메일", example = "student1@example.com")
    private String studentEmail;

    @Schema(description = "반 등록 일시", example = "2026-05-26T10:00:00")
    private LocalDateTime joinedAt;

    public static ClassStudentResponse from(ClassStudent classStudent) {
        ClassStudentResponse dto = new ClassStudentResponse();
        dto.studentId    = classStudent.getStudent().getUserId();
        dto.studentName  = classStudent.getStudent().getName();
        dto.studentEmail = classStudent.getStudent().getEmail();
        dto.joinedAt     = classStudent.getJoinedAt();
        return dto;
    }
}
