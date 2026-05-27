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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Assignment", description = "과제 관련 API")
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final ImageService imageService;

    // 선생: 과제지/답지 업로드용 presigned URL 발급
    // GET /api/assignments/upload-url?type=problem&ext=pdf
    @Operation(summary = "교사: 문제지/답지 업로드 URL 발급",
                description = "S3에 파일을 직접 업로드할 수 있는 Presigned URL 발급. " +
            "반환된 presignedUrl로 PUT 요청하여 파일 업로드 후 s3Key를 과제 생성에 사용")
    @GetMapping("/upload-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getTeacherUploadUrl(
            @Parameter(description = "파일 종류",example = "problem 나 answer중 1개 ")
            @RequestParam String type,
            @Parameter(description = "파일 확장자",example = "pdf,jpg,png,heic")
            @RequestParam String ext
    ) {
        PresignedUrlResponse response = imageService.getTeacherUploadUrl(type, ext);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.PRESIGNED_URL_GENERATED, response));
    }

    // 선생: 과제 생성 (problemS3Key, answerS3Key는 위 URL로 업로드 후 받은 값 사용)
    // POST /api/assignments
    @Operation(
            summary = "교사: 과제 생성",
            description = "문제지/답지 업로드 후 받은 s3Key를 포함하여 과제 생성. 생성 직후 status = DRAFT"
    )
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
    @Operation(
            summary = "과제 목록 조회",
            description = "교사: DRAFT + PUBLISHED 전체 조회 + 채점 집계 포함. " +
                    "학생: PUBLISHED 과제만 조회"
    )
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
    @Operation(
            summary = "과제 상세 조회",
            description = "교사: 문제지 URL + 답지 URL + 문항 정답/채점기준 포함. " +
                    "학생: 문제지 URL만 + 문항 내용만 (정답/채점기준 미포함)"
    )
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
    @Operation(
            summary = "교사: 과제 수정",
            description = "DRAFT 상태인 과제만 수정 가능. PUBLISHED 상태이면 수정 불가"
    )
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
    @Operation(
            summary = "교사: 과제 삭제",
            description = "본인이 생성한 과제만 삭제 가능. 삭제 시 문항도 같이 삭제됨"
    )
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
    @Operation(
            summary = "교사: 과제 게시",
            description = "DRAFT → PUBLISHED 상태 변경. 게시 후 학생 과제 목록에 노출됨"
    )
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
    @Operation(
            summary = "교사: 문항 정답/채점기준 수정",
            description = "특정 과제의 특정 문항 정답과 채점기준 수정. 본인 과제만 수정 가능"
    )
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
    @Operation(
            summary = "학생: 풀이 업로드 URL 발급",
            description = "S3에 풀이 이미지를 직접 업로드할 수 있는 Presigned URL 발급. " +
                    "반환된 presignedUrl로 PUT 요청하여 파일 업로드 후 confirm API 호출"
    )
    @PostMapping("/{assignmentId}/submissions/upload-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getStudentUploadUrl(
            @Parameter(description = "과제 ID", example = "1")
            @PathVariable Long assignmentId,
            @Parameter(description = "파일 확장자", example = "jpg,pdf,png")
            @RequestParam String ext,
            @AuthenticationPrincipal Long studentId
    ) {
        PresignedUrlResponse response = imageService.getStudentUploadUrl(assignmentId, studentId, ext);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessCode.SUBMISSION_CREATED, response));
    }

    // 학생: 업로드 완료 확인 → gradingStatus = PENDING
    // POST /api/assignments/{id}/submissions/{submissionId}/confirm
    //todo : AI 파트구현 담당자가 해당 메서드 불필요시 삭제 가능합니다 !
    @Operation(
            summary = "학생: 풀이 업로드 완료 확인",
            description = "S3 업로드 완료 후 서버에 확인 신호 전송. " +
                    "gradingStatus = PENDING 으로 변경 → AI 채점 시작"
    )
    @PostMapping("/{assignmentId}/submissions/{submissionId}/confirm")
    public ResponseEntity<ApiResponse<SubmissionResponse>> confirmUpload(
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId
    ) {
        SubmissionResponse response = imageService.confirmUpload(assignmentId, submissionId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.SUBMISSION_CONFIRMED, response));
    }
}