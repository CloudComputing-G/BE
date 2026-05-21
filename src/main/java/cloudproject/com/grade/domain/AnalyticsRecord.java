package cloudproject.com.grade.domain;

import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.auth.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalyticsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analyticsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    private String questionType;
    private Integer errorCount;
    private Integer totalCount;
    private Float predictedErrorRate;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
}
