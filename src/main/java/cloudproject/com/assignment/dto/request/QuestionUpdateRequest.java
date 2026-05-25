package cloudproject.com.assignment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class QuestionUpdateRequest {
    @NotBlank(message = "정답은 필수입니다")
    private String answer;

    private String gradingCriteria;
}
