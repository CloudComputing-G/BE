package cloudproject.com.grade.controller;

import cloudproject.com.auth.domain.Role;
import cloudproject.com.auth.support.AuthenticationSupport;
import cloudproject.com.grade.dto.RegradeConfirmRequest;
import cloudproject.com.grade.dto.RegradeConfirmResponse;
import cloudproject.com.grade.dto.RegradeResponse;
import cloudproject.com.grade.service.RegradeService;
import cloudproject.com.global.common.ApiResponse;
import cloudproject.com.global.common.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class RegradeController {

    private final RegradeService regradeService;

    @PostMapping("/{sid}/questions/{qid}/regrade")
    public ResponseEntity<ApiResponse<RegradeResponse>> requestRegrade(
            @PathVariable("sid") Long submissionId,
            @PathVariable("qid") Long questionId,
            @AuthenticationPrincipal Long currentUserId,
            Authentication authentication
    ) {
        Role role = AuthenticationSupport.extractRole(authentication);
        RegradeResponse response =
                regradeService.requestRegrade(submissionId, questionId, currentUserId, role);

        return ResponseEntity.ok(
                ApiResponse.success(SuccessCode.REGRADE_REQUEST_SUCCESS, response)
        );
    }

    @PatchMapping("/{sid}/questions/{qid}/regrade/confirm")
    public ResponseEntity<ApiResponse<RegradeConfirmResponse>> confirmRegrade(
            @PathVariable("sid") Long submissionId,
            @PathVariable("qid") Long questionId,
            @RequestBody(required = false) RegradeConfirmRequest request,
            @AuthenticationPrincipal Long currentUserId,
            Authentication authentication
    ) {
        Role role = AuthenticationSupport.extractRole(authentication);
        Integer score = request == null ? null : request.score();
        RegradeConfirmResponse response =
                regradeService.confirmRegrade(submissionId, questionId, score, currentUserId, role);

        return ResponseEntity.ok(
                ApiResponse.success(SuccessCode.REGRADE_CONFIRM_SUCCESS, response)
        );
    }
}
