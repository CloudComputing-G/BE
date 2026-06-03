package cloudproject.com.assignment.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(columnDefinition = "TEXT")
    private String gradingCriteria;

    private Integer maxScore;
    private Integer orderNum;
    private String questionType;

    public void update(String answer, String gradingCriteria) {
        this.answer = answer;
        this.gradingCriteria = gradingCriteria;
    }
}
