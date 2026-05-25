package cloudproject.com.grade.dto;

import cloudproject.com.grade.domain.RegradeStatus;

public record RegradeResponse(
        Long questionId,
        RegradeStatus regradeStatus
) {
}
