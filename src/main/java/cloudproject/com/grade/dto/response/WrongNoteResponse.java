package cloudproject.com.grade.dto.response;

import cloudproject.com.grade.domain.WrongNote;
import cloudproject.com.grade.domain.QuestionResult;
import cloudproject.com.assignment.domain.Question;
import cloudproject.com.assignment.domain.Assignment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WrongNoteResponse {

    private Long wrongNoteId;
    private LocalDateTime createdAt;

    private Long resultId;
    private String result;
    private Integer score;
    private String reason;
    private String imageUrl;
    private String regradeStatus;

    private Long questionId;
    private String content;
    private Integer orderNum;
    private Integer maxScore;

    private Long assignmentId;
    private String title;
    private String subject;

    public static WrongNoteResponse from(WrongNote wrongNote) {
        WrongNoteResponse dto = new WrongNoteResponse();
        dto.wrongNoteId = wrongNote.getWrongNoteId();
        dto.createdAt = wrongNote.getCreatedAt();

        QuestionResult questionResult = wrongNote.getResult();
        dto.resultId = questionResult.getResultId();
        dto.result = questionResult.getResult();
        dto.score = questionResult.getScore();
        dto.reason = questionResult.getReason();
        dto.imageUrl = questionResult.getImageUrl();
        dto.regradeStatus = questionResult.getRegradeStatus();

        Question question = questionResult.getQuestion();
        dto.questionId = question.getQuestionId();
        dto.content = question.getContent();
        dto.orderNum = question.getOrderNum();
        dto.maxScore = question.getMaxScore();

        Assignment assignment = question.getAssignment();
        dto.assignmentId = assignment.getAssignmentId();
        dto.title = assignment.getTitle();
        dto.subject = assignment.getSubject();

        return dto;
    }
}
