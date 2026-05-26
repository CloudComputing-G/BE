package cloudproject.com.classroom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "반 수정 요청")
@Getter
@NoArgsConstructor
public class ClassroomUpdateRequest {

    @Schema(description = "수정할 반 이름", example = "1학년 2반")
    @NotBlank(message = "반 이름은 필수입니다.")
    private String name;
}
