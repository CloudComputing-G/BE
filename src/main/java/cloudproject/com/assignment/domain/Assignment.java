package cloudproject.com.assignment.domain;

import cloudproject.com.auth.domain.User;
import cloudproject.com.classroom.domain.Classroom;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    private String title;
    private String subject;
    private String status;
    private LocalDateTime dueDate;
    private String problemS3Key;
    private String answerS3Key;
    private LocalDateTime createdAt;
}
