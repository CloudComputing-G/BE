package cloudproject.com.grade.dto;

import cloudproject.com.grade.domain.RegradeStatus;
import cloudproject.com.grade.domain.Result;

public record RegradeConfirmResponse(
        Long questionId,
        Integer score,
        Result result,
        RegradeStatus regradeStatus,
        Integer totalScore
) {
}
