package cloudproject.com.classroom.domain;

import cloudproject.com.auth.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "class_students")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClassStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Classroom classroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
    }

    public static ClassStudent create(Classroom classroom, User student) {
        ClassStudent cs = new ClassStudent();
        cs.classroom = classroom;
        cs.student = student;
        return cs;
    }
}
