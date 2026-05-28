package cloudproject.com.assignment.dto.response;

import lombok.Getter;

@Getter
public class SimilarProblemResponse {

    private String similarProblem;

    public static SimilarProblemResponse of(String similarProblem) {
        SimilarProblemResponse dto = new SimilarProblemResponse();
        dto.similarProblem = similarProblem;
        return dto;
    }
}
