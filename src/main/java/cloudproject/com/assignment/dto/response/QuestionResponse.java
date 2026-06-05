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
    private String questionType;
    private String category;
    private String detectedType;

    // 학생용 ( 정답 / 채점 기준 제외)
    public static QuestionResponse from(Question question) {
        QuestionResponse dto = new QuestionResponse();
        dto.questionId = question.getQuestionId();
        dto.content = question.getContent();
        dto.maxScore = question.getMaxScore();
        dto.orderNum = question.getOrderNum();
        dto.questionType = question.getQuestionType();
        dto.category = question.getCategory();
        dto.detectedType = question.getDetectedType();
        return dto;
    }
    // 정답/채점기준 포함 (교사용)
    public static QuestionResponse withAnswer(Question question) {
        QuestionResponse dto = from(question);
        dto.answer = question.getAnswer();
        dto.gradingCriteria = question.getGradingCriteria();
        return dto;
    }
}
