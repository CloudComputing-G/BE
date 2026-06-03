package cloudproject.com.grade.domain;

import cloudproject.com.assignment.domain.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    private String result;
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private String imageUrl;
    private String regradeStatus;
    private Boolean needsManualReview;

    public static QuestionResult of(
            Submission submission, Question question,
            int score, String result, String reason, String imageUrl,
            Boolean needsManualReview
    ) {
        QuestionResult qr = new QuestionResult();
        qr.submission = submission;
        qr.question = question;
        qr.score = score;
        qr.result = result;
        qr.reason = reason;
        qr.imageUrl = imageUrl;
        qr.needsManualReview = needsManualReview;
        return qr;
    }

    public void requestRegrade() {
        this.regradeStatus = "PENDING";
    }

    public void rejectRegrade() {
        this.regradeStatus = "DONE";
    }

    public void confirmRegradeWithScore(int newScore, String newResult) {
        this.score = newScore;
        this.result = newResult;
        this.regradeStatus = "DONE";
    }
}
