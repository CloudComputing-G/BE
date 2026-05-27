package cloudproject.com.assignment.service;


import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.assignment.domain.Question;
import cloudproject.com.assignment.dto.request.AssignmentCreateRequest;
import cloudproject.com.assignment.dto.request.AssignmentUpdateRequest;
import cloudproject.com.assignment.dto.request.QuestionUpdateRequest;
import cloudproject.com.assignment.dto.response.AssignmentResponse;
import cloudproject.com.assignment.repository.AssignmentRepository;
import cloudproject.com.assignment.repository.QuestionRepository;
import cloudproject.com.auth.domain.User;
import cloudproject.com.auth.repository.UserRepository;
import cloudproject.com.classroom.domain.Classroom;
import cloudproject.com.classroom.repository.ClassStudentRepository;
import cloudproject.com.classroom.repository.ClassroomRepository;
import cloudproject.com.global.common.code.ErrorCode;
import cloudproject.com.global.error.BusinessException;
import cloudproject.com.global.s3.S3Service;
import cloudproject.com.grade.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final QuestionRepository questionRepository;
    private final SubmissionRepository submissionRepository;
    private final ClassStudentRepository classStudentRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final S3Service s3Service;

    @Transactional //쓰기 post/assignments 과제 생성 - 초안저장
    public AssignmentResponse createAssignment(AssignmentCreateRequest request, Long teacherId){

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        Classroom classroom = classroomRepository.findById(request.getClassId())
                .orElseThrow(()->new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND));

        Assignment assignment = Assignment.builder()
                .classroom(classroom) // classId로 Class 조회 필요
                .teacher(teacher)
                .title(request.getTitle())
                .subject(request.getSubject())
                .dueDate(request.getDueDate())
                .problemS3Key(request.getProblemS3Key())
                .answerS3Key(request.getAnswerS3Key())
                .build();

        if (request.getQuestions() != null) {
            request.getQuestions().forEach(q -> {
                Question question = Question.builder()
                        .assignment(assignment)
                        .content(q.getContent())
                        .answer(q.getAnswer())
                        .gradingCriteria(q.getGradingCriteria())
                        .maxScore(q.getMaxScore())
                        .orderNum(q.getOrderNum())
                        .build();
                assignment.getQuestions().add(question);
            });
        }

        Assignment saved = assignmentRepository.save(assignment);
        return AssignmentResponse.from(saved);
    }

    // 반별 과제 목록 조회 (GET /assignments?classId=1)
    public List<AssignmentResponse> getAssignments(Long classId,  Long userId, boolean isTeacher){
        if (isTeacher) {
            // 교사: 본인 과제 (DRAFT + PUBLISHED 전부)
            User teacher = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));
            // 반 전체 학생 수
            long totalCount = classStudentRepository.countByClassroom_ClassId(classId);
            //반별 과제 목록
            return assignmentRepository.findByClassroom_ClassIdAndTeacher(classId, teacher)
                    .stream()
                    .map(assignment -> {
                        long submittedCount = submissionRepository
                                .countByAssignment_AssignmentIdAndGradingStatusIn(
                                        assignment.getAssignmentId(), List.of("PENDING", "DONE"));
                        long gradedCount = submissionRepository
                                .countByAssignment_AssignmentIdAndGradingStatus(
                                        assignment.getAssignmentId(), "DONE");
                        return AssignmentResponse.of(assignment, totalCount, submittedCount, gradedCount);
                    })
                    .collect(Collectors.toList());
        }else {
            // 학생: PUBLISHED 과제만
            return assignmentRepository.findByClassroom_ClassIdAndStatus(classId, "PUBLISHED")
                    .stream()
                    .map(AssignmentResponse::from)
                    .collect(Collectors.toList());
        }
    }

    // 과제 상세 조회 (GET /assignments/{id})
    public AssignmentResponse getAssignment(Long assignmentId,boolean isTeacher){
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        String problemUrl = assignment.getProblemS3Key() != null
                ? s3Service.getDownloadUrl(assignment.getProblemS3Key())
                : null;

        String answerUrl = null;

        if (isTeacher && assignment.getAnswerS3Key() != null) {
            answerUrl = s3Service.getDownloadUrl(assignment.getAnswerS3Key());
        }

        return AssignmentResponse.withUrl(assignment, problemUrl, answerUrl,isTeacher);
    }

    //과제 수정 (put/assignments/{id})
    @Transactional
    public AssignmentResponse updateAssingment(Long assignmentId, AssignmentUpdateRequest request, Long teacherId){
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if (!assignment.getTeacher().getUserId().equals(teacherId)) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_FORBIDDEN);
        }
        if ("PUBLISHED".equals(assignment.getStatus())) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_ALREADY_PUBLISHED);
        }

        assignment.update(request.getTitle(), request.getDueDate());
        return AssignmentResponse.from(assignment);

    }

    //과제 삭제 (Delete/assignments/{id})
    @Transactional
    public void deleteAssignment(Long assignmentId, Long teacherId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if (!assignment.getTeacher().getUserId().equals(teacherId)) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_FORBIDDEN);
        }
        assignmentRepository.delete(assignment);
    }

    // 문항 수정 (PUT /assignments/{id}/questions/{qid})
    @Transactional
    public void updateQuestion(Long assignmentId, Long questionId, QuestionUpdateRequest request, Long teacherId) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if (!assignment.getTeacher().getUserId().equals(teacherId)) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_FORBIDDEN);
        }
        Question question = questionRepository
                .findByQuestionIdAndAssignment_AssignmentId(questionId, assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        question.update(request.getAnswer(), request.getGradingCriteria());
    }

    // 과제 게시 (POST /assignments/{id}/publish) 게시까지
    @Transactional
    public AssignmentResponse publishAssignment(Long assignmentId, Long teacherId) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if (!assignment.getTeacher().getUserId().equals(teacherId)) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_FORBIDDEN);
        }

        if ("PUBLISHED".equals(assignment.getStatus())) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_ALREADY_PUBLISHED);
        }

        assignment.publish();
        return AssignmentResponse.from(assignment);
    }

}
