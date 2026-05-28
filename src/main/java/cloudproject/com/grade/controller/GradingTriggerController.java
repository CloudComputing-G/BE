package cloudproject.com.grade.controller;

import cloudproject.com.grade.dto.GradingResultRequest;
import cloudproject.com.grade.service.GradingService;
import cloudproject.com.global.common.ApiResponse;
import cloudproject.com.global.common.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/submissions")
@RequiredArgsConstructor
public class GradingTriggerController {

    private final GradingService gradingService;

    @PostMapping("/{sid}/grade")
    public ResponseEntity<ApiResponse<Void>> triggerGrading(@PathVariable("sid") Long submissionId) {
        gradingService.startGrading(submissionId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GRADING_TRIGGERED));
    }

    @PostMapping("/{sid}/result")
    public ResponseEntity<ApiResponse<Void>> reportResult(
            @PathVariable("sid") Long submissionId,
            @RequestBody GradingResultRequest request
    ) {
        gradingService.reportResult(submissionId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.GRADING_RESULT_REPORTED));
    }
}
