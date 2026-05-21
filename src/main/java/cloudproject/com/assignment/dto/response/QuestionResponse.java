package cloudproject.com.assignment.dto.response;

import cloudproject.com.assignment.domain.Question;
import lombok.Getter;

@Getter
public class QuestionResponse {

    private Long questionId;
    private String content;
    private String answer;
    private String gradingCriteria;
    private Integer maxScore;
    private Integer orderNum;

    public static QuestionResponse from(Question question) {
        QuestionResponse dto = new QuestionResponse();
        dto.questionId = question.getQuestionId();
        dto.content = question.getContent();
        dto.answer = question.getAnswer();
        dto.gradingCriteria = question.getGradingCriteria();
        dto.maxScore = question.getMaxScore();
        dto.orderNum = question.getOrderNum();
        return dto;
    }

}
