package cloudproject.com.grade.domain;

import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.auth.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    private String s3Key;

    @Enumerated(EnumType.STRING)
    private GradingStatus gradingStatus;

    private Integer totalScore;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private String failReason;

    public void updateTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
