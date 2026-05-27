package cloudproject.com.grade.controller;

import cloudproject.com.global.common.ApiResponse;
import cloudproject.com.global.common.code.SuccessCode;
import cloudproject.com.grade.dto.response.AssignmentAnalyticsResponse;
import cloudproject.com.grade.dto.response.WeakPointResponse;
import cloudproject.com.grade.service.AnalyticsRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Analytics", description = "취약점 분석 관련 API")
@RestController
@RequiredArgsConstructor
public class AnalyticsRecordController {

    private final AnalyticsRecordService analyticsRecordService;

    @Operation(
            summary = "내 취약점 분석 목록 조회",
            description = "JWT 토큰에서 추출한 학생 ID로 본인의 취약점 분석 목록을 조회합니다. 예측 오답률 높은 순으로 정렬됩니다."
    )
    @GetMapping("/api/students/me/weak-points")
    public ResponseEntity<ApiResponse<List<WeakPointResponse>>> getMyWeakPoints(
            @AuthenticationPrincipal Long studentId
    ) {
        List<WeakPointResponse> response = analyticsRecordService.getMyWeakPoints(studentId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.WEAK_POINTS_FETCHED, response));
    }

    @Operation(
            summary = "과제별 전체 학생 통계 조회",
            description = "특정 과제에 대한 전체 학생의 분석 통계를 조회합니다."
    )
    @GetMapping("/api/assignments/{assignmentId}/analytics")
    public ResponseEntity<ApiResponse<List<AssignmentAnalyticsResponse>>> getAssignmentAnalytics(
            @PathVariable Long assignmentId
    ) {
        List<AssignmentAnalyticsResponse> response = analyticsRecordService.getAssignmentAnalytics(assignmentId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ASSIGNMENT_ANALYTICS_FETCHED, response));
    }
}
