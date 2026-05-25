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

    @Enumerated(EnumType.STRING)
    private Result result;

    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private RegradeStatus regradeStatus;

    public void requestRegrade() {
        this.regradeStatus = RegradeStatus.PENDING;
    }

    public void rejectRegrade() {
        this.regradeStatus = RegradeStatus.DONE;
    }

    public void confirmRegradeWithScore(int newScore, Result newResult) {
        this.score = newScore;
        this.result = newResult;
        this.regradeStatus = RegradeStatus.DONE;
    }
}
