package cloudproject.com.assignment.service;

import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.assignment.dto.PresignedUrlResponse;
import cloudproject.com.assignment.dto.response.SubmissionResponse;
import cloudproject.com.assignment.repository.AssignmentRepository;
import cloudproject.com.auth.domain.User;
import cloudproject.com.auth.repository.UserRepository;
import cloudproject.com.global.common.code.ErrorCode;
import cloudproject.com.global.error.BusinessException;
import cloudproject.com.global.s3.S3Service;
import cloudproject.com.global.s3.S3uploadResult;
import cloudproject.com.grade.domain.Submission;
import cloudproject.com.grade.repository.SubmissionRepository;
import cloudproject.com.grade.service.GradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {
    private final S3Service s3Service;
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final GradingService gradingService;

    public PresignedUrlResponse getTeacherUploadUrl(String type, String fileExtension) {
        S3uploadResult result = type.equals("problem")
                ? s3Service.getTeacherProblemUploadUrl(fileExtension)
                : s3Service.getTeacherAnswerUploadUrl(fileExtension);
        return PresignedUrlResponse.of(null, result.getPresignedUrl(), result.getS3Key());
    }

    @Transactional
    public PresignedUrlResponse getStudentUploadUrl(Long assignmentId, Long studentId, String fileExtension) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        // 이미 제출한 적 있으면 기존 Submission에 새 URL 발급 (중복 레코드 방지)
        Submission submission = submissionRepository
                .findByAssignment_AssignmentIdAndStudent_UserId(assignmentId, studentId)
                .orElseGet(() -> submissionRepository.save(
                        Submission.builder()
                                .assignment(assignment)
                                .student(student)
                                .build()
                ));

        S3uploadResult result = s3Service.getStudentUploadUrl(assignmentId, studentId, fileExtension);
        submission.updateS3Key(result.getS3Key());

        return PresignedUrlResponse.of(submission.getSubmissionId(), result.getPresignedUrl(), result.getS3Key());
    }

    @Transactional // s3 업로드 성공됐는지 자동으로 호출
    public SubmissionResponse confirmUpload(Long assignmentId, Long submissionId) {

        Submission submission = submissionRepository
                .findBySubmissionIdAndAssignment_AssignmentId(submissionId, assignmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBMISSION_NOT_FOUND));

        submission.confirmUpload();
        gradingService.startGrading(submission.getSubmissionId());

        return SubmissionResponse.from(submission);
    }
}