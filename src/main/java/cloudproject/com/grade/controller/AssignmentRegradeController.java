package cloudproject.com.grade.controller;

import cloudproject.com.auth.domain.Role;
import cloudproject.com.auth.support.AuthenticationSupport;
import cloudproject.com.grade.dto.RegradeRequestListResponse;
import cloudproject.com.grade.service.RegradeService;
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
public class AssignmentRegradeController {

    private final RegradeService regradeService;

    @GetMapping("/{id}/regrade-requests")
    public ResponseEntity<ApiResponse<RegradeRequestListResponse>> getPendingRegradeRequests(
            @PathVariable("id") Long assignmentId,
            @AuthenticationPrincipal Long currentUserId,
            Authentication authentication
    ) {
        Role role = AuthenticationSupport.extractRole(authentication);
        RegradeRequestListResponse response =
                regradeService.getPendingRegradeRequests(assignmentId, currentUserId, role);

        return ResponseEntity.ok(
                ApiResponse.success(SuccessCode.REGRADE_REQUEST_LIST_FETCH_SUCCESS, response)
        );
    }
}
