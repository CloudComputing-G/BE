package cloudproject.com.assignment.controller;

import cloudproject.com.assignment.dto.request.ChatRequest;
import cloudproject.com.assignment.dto.response.ChatMessageResponse;
import cloudproject.com.assignment.dto.response.SimilarProblemResponse;
import cloudproject.com.assignment.service.ChatService;
import cloudproject.com.global.common.ApiResponse;
import cloudproject.com.global.common.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chat", description = "AI 튜터 챗봇 API")
@RestController
@RequestMapping("/api/questions/{questionId}/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(
            summary = "AI 튜터에게 질문",
            description = "Amazon Bedrock Claude를 호출하여 수학 문제 풀이 도움을 받습니다. 이전 대화 기록이 context로 포함됩니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ChatMessageResponse>> chat(
            @PathVariable Long questionId,
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal Long studentId
    ) {
        ChatMessageResponse response = chatService.chat(questionId, studentId, request.getMessage());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CHAT_SUCCESS, response));
    }

    @Operation(
            summary = "유사문제 생성",
            description = "문항의 내용과 채점 기준을 Bedrock에 전달하여 유사한 새 문제를 생성합니다."
    )
    @GetMapping("/similar-problem")
    public ResponseEntity<ApiResponse<SimilarProblemResponse>> getSimilarProblem(
            @PathVariable Long questionId
    ) {
        SimilarProblemResponse response = chatService.generateSimilarProblem(questionId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.SIMILAR_PROBLEM_GENERATED, response));
    }

    @Operation(
            summary = "대화 기록 조회",
            description = "특정 문항에 대한 본인의 AI 튜터 대화 기록을 시간 순으로 조회합니다."
    )
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getChatHistory(
            @PathVariable Long questionId,
            @AuthenticationPrincipal Long studentId
    ) {
        List<ChatMessageResponse> response = chatService.getChatHistory(questionId, studentId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CHAT_HISTORY_FETCHED, response));
    }
}
