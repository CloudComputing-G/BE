package cloudproject.com.grade.domain;

import cloudproject.com.assignment.domain.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "regrade_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegradeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regradeRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    private String requestedBy;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private Integer originalScore;
    private Integer confirmedScore;
    private String regradeStatus;

    @Column(columnDefinition = "TEXT")
    private String teacherComment;

    private LocalDateTime requestedAt;
    private LocalDateTime confirmedAt;
}
