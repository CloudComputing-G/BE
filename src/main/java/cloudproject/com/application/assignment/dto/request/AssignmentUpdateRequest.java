package cloudproject.com.application.assignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AssignmentUpdateRequest {
    @NotBlank(message = "제목은 필수입니다")
    private String title;
    private LocalDateTime dueDate;
}
