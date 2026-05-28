package cloudproject.com.classroom.dto.response;

import cloudproject.com.classroom.domain.Classroom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "반 응답")
@Getter
public class ClassroomResponse {

    @Schema(description = "반 ID", example = "1")
    private Long classId;

    @Schema(description = "반 이름", example = "1학년 1반")
    private String name;

    @Schema(description = "담당 선생님 이름", example = "김선생")
    private String teacherName;

    @Schema(description = "반 생성 일시", example = "2026-05-26T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "반 학생 수 (선생님 조회 시 포함)", example = "25")
    private long studentCount;

    // 기본 변환 (단순 조회)
    public static ClassroomResponse from(Classroom classroom) {
        ClassroomResponse dto = new ClassroomResponse();
        dto.classId = classroom.getClassId();
        dto.name = classroom.getName();
        dto.teacherName = classroom.getTeacher().getName();
        dto.createdAt = classroom.getCreatedAt();
        return dto;
    }

    // 학생 수 포함 변환 (교사용)
    public static ClassroomResponse of(Classroom classroom, long studentCount) {
        ClassroomResponse dto = from(classroom);
        dto.studentCount = studentCount;
        return dto;
    }
}
