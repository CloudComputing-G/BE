package cloudproject.com.classroom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "반 학생 추가 요청")
@Getter
@NoArgsConstructor
public class ClassStudentAddRequest {

    @Schema(
            description = "추가할 학생 이메일 목록 (1명 이상)",
            example = "[\"student1@example.com\", \"student2@example.com\"]"
    )
    @NotEmpty(message = "추가할 학생 이메일을 1개 이상 입력해야 합니다.")
    private List<String> studentEmails;
}
