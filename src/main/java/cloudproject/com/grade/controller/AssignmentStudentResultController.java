package cloudproject.com.grade.controller;

import cloudproject.com.auth.domain.Role;
import cloudproject.com.auth.support.AuthenticationSupport;
import cloudproject.com.grade.dto.SubmissionResultResponse;
import cloudproject.com.grade.service.SubmissionResultService;
import cloudproject.com.global.common.ApiResponse;
import cloudproject.com.global.common.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentStudentResultController {

    private final SubmissionResultService submissionResultService;

    @GetMapping("/{id}/students/{uid}")
    public ResponseEntity<ApiResponse<SubmissionResultResponse>> getStudentResult(
            @PathVariable("id") Long assignmentId,
            @PathVariable("uid") Long studentId,
            @AuthenticationPrincipal Long currentUserId,
            Authentication authentication
    ) {
        Role role = AuthenticationSupport.extractRole(authentication);
        SubmissionResultResponse response = submissionResultService
                .getStudentSubmissionResult(assignmentId, studentId, currentUserId, role);

        return ResponseEntity.ok(
                ApiResponse.success(SuccessCode.SUBMISSION_RESULT_FETCH_SUCCESS, response)
        );
    }
}
