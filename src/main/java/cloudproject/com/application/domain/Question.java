package cloudproject.com.application.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    // Question N개 → Assignment 1개 (반대방향)
    // FetchType.LAZY = Question 조회할 때 Assignment는 바로 안 가져옴
    // 실제로 getAssignment() 호출할 때 그때 DB 조회 (성능상 유리)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "grading_criteria", columnDefinition = "TEXT")
    private String gradingCriteria;

    @Column(name = "max_score")
    private Integer maxScore;

    @Column(name = "order_num")
    private Integer orderNum;

    public void update(String answer, String gradingCriteria) {
        this.answer = answer;
        this.gradingCriteria = gradingCriteria;
    }
}