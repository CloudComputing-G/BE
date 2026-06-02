package cloudproject.com.grade.dto;

public record RegradeConfirmResponse(
        Long questionId,
        Integer score,
        String result,
        String regradeStatus,
        Integer totalScore
) {
}
