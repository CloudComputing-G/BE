package cloudproject.com.grade.dto;

public record RegradeResponse(
        Long questionId,
        String regradeStatus
) {
}
