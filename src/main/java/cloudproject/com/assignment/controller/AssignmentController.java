package cloudproject.com.assignment.controller;

import cloudproject.com.assignment.dto.PresignedUrlResponse;
import cloudproject.com.assignment.dto.request.AssignmentCreateRequest;
import cloudproject.com.assignment.dto.request.AssignmentUpdateRequest;
import cloudproject.com.assignment.dto.request.QuestionUpdateRequest;
import cloudproject.com.assignment.dto.response.AssignmentResponse;
import cloudproject.com.assignment.dto.response.SubmissionResponse;
import cloudproject.com.assignment.service.AssignmentService;
import cloudproject.com.assignment.service.ImageService;
import cloudproject.com.global.common.ApiResponse;
import cloudproject.com.global.common.code.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final ImageService imageService;

    // 선생: 과제지/답지 업로드용 presigned URL 발급
    // GET /api/assignments/upload-url?type=problem&ext=pdf
    @GetMapping("/upload-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getTeacherUploadUrl(
            @RequestParam String type,
            @RequestParam String ext
    ) {
        PresignedUrlResponse response = imageService.getTeacherUploadUrl(type, ext);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.PRESIGNED_URL_GENERATED, response));
    }

    // 선생: 과제 생성 (problemS3Key, answerS3Key는 위 URL로 업로드 후 받은 값 사용)
    // POST /api/assignments
    @PostMapping
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(
            @Valid @RequestBody AssignmentCreateRequest request,
            @AuthenticationPrincipal Long teacherId
    ) {
        AssignmentResponse response = assignmentService.createAssignment(request, teacherId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessCode.ASSIGNMENT_CREATED, response));
    }

    // 반별 과제 목록 조회
    // GET /api/assignments?classId=1
    @GetMapping
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignments(
            @RequestParam Long classId,
            @AuthenticationPrincipal Long userId,
            Authentication authentication
    ) {
        boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        List<AssignmentResponse> response = assignmentService.getAssignments(classId, userId, isTeacher);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ASSIGNMENTS_FETCHED, response));
    }


    // 과제 상세 조회
    // GET /api/assignments/{id}
    @GetMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignment(
            @PathVariable Long assignmentId , @AuthenticationPrincipal Long userId , Authentication authentication
            ) {
        boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        AssignmentResponse response = assignmentService.getAssignment(assignmentId,isTeacher);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ASSIGNMENTS_FETCHED, response));
    }

    // 과제 수정
    // PUT /api/assignments/{id}
    @PutMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody AssignmentUpdateRequest request,
            @AuthenticationPrincipal Long teacherId
    ) {
        AssignmentResponse response = assignmentService.updateAssingment(assignmentId, request, teacherId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ASSIGNMENT_UPDATED, response));
    }

    // 과제 삭제
    // DELETE /api/assignments/{id}
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal Long teacherId
    ) {
        assignmentService.deleteAssignment(assignmentId, teacherId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ASSIGNMENT_DELETED));
    }

    // 과제 게시
    // POST /api/assignments/{id}/publish
    @PostMapping("/{assignmentId}/publish")
    public ResponseEntity<ApiResponse<AssignmentResponse>> publishAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal Long teacherId
    ) {
        AssignmentResponse response = assignmentService.publishAssignment(assignmentId, teacherId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ASSIGNMENT_PUBLISHED, response));
    }

    // 문항 수정
    // PUT /api/assignments/{id}/questions/{qid}
    @PutMapping("/{assignmentId}/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> updateQuestion(
            @PathVariable Long assignmentId,
            @PathVariable Long questionId,
            @RequestBody QuestionUpdateRequest request,
            @AuthenticationPrincipal Long teacherId
    ) {
        assignmentService.updateQuestion(assignmentId, questionId, request, teacherId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.QUESTION_UPDATED));
    }

    // 학생: 제출용 presigned URL 발급 + Submission 생성
    // POST /api/assignments/{id}/submissions/upload-url?ext=jpg
    @PostMapping("/{assignmentId}/submissions/upload-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getStudentUploadUrl(
            @PathVariable Long assignmentId,
            @RequestParam String ext,
            @AuthenticationPrincipal Long studentId
    ) {
        PresignedUrlResponse response = imageService.getStudentUploadUrl(assignmentId, studentId, ext);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessCode.SUBMISSION_CREATED, response));
    }

    // 학생: 업로드 완료 확인 → gradingStatus = PENDING
    // POST /api/assignments/{id}/submissions/{submissionId}/confirm
    @PostMapping("/{assignmentId}/submissions/{submissionId}/confirm")
    public ResponseEntity<ApiResponse<SubmissionResponse>> confirmUpload(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId
    ) {
        SubmissionResponse response = imageService.confirmUpload(assignmentId, submissionId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.SUBMISSION_CONFIRMED, response));
    }
}