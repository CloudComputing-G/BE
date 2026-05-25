package cloudproject.com.grade.domain;

import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
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
    private String gradingStatus;
    private Integer totalScore;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public static Submission create(Assignment assignment, User student) {
        Submission s = new Submission();
        s.assignment = assignment;
        s.student = student;
        return s;
    }

    public void updateS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public void confirmUpload() {
        this.gradingStatus = "PENDING";
    }
}
