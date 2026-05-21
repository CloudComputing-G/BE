package cloudproject.com.grade.domain;

import cloudproject.com.auth.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "wrong_notes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WrongNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wrongNoteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private QuestionResult result;

    private LocalDateTime createdAt;
}
