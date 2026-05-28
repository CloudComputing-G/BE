package cloudproject.com.assignment.service;

import cloudproject.com.assignment.domain.ChatHistory;
import cloudproject.com.assignment.domain.Question;
import cloudproject.com.assignment.dto.response.ChatMessageResponse;
import cloudproject.com.assignment.dto.response.SimilarProblemResponse;
import cloudproject.com.assignment.repository.ChatHistoryRepository;
import cloudproject.com.assignment.repository.QuestionRepository;
import cloudproject.com.auth.domain.User;
import cloudproject.com.auth.repository.UserRepository;
import cloudproject.com.global.common.code.ErrorCode;
import cloudproject.com.global.error.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private static final String MODEL_ID = "anthropic.claude-3-5-sonnet-20241022-v2:0";
    private static final String SYSTEM_PROMPT = "너는 학생의 수학 문제 풀이를 도와주는 AI 튜터야";

    private final ChatHistoryRepository chatHistoryRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final BedrockRuntimeClient bedrockRuntimeClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public ChatMessageResponse chat(Long questionId, Long studentId, String userMessage) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED));

        List<ChatHistory> history = chatHistoryRepository
                .findByStudent_UserIdAndQuestion_QuestionIdOrderByCreatedAtAsc(studentId, questionId);

        String assistantReply = invokeBedrockClaude(history, userMessage);

        chatHistoryRepository.save(ChatHistory.create(student, question, "user", userMessage));
        ChatHistory assistantHistory = chatHistoryRepository.save(
                ChatHistory.create(student, question, "assistant", assistantReply));

        return ChatMessageResponse.from(assistantHistory);
    }

    public List<ChatMessageResponse> getChatHistory(Long questionId, Long studentId) {
        return chatHistoryRepository
                .findByStudent_UserIdAndQuestion_QuestionIdOrderByCreatedAtAsc(studentId, questionId)
                .stream()
                .map(ChatMessageResponse::from)
                .collect(Collectors.toList());
    }

    public SimilarProblemResponse generateSimilarProblem(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        String prompt = String.format(
                "다음 문항과 유사한 새로운 문제를 1개 만들어줘.\n\n[문항 내용]\n%s\n\n[채점 기준]\n%s",
                question.getContent(),
                question.getGradingCriteria() != null ? question.getGradingCriteria() : "없음"
        );

        String result = invokeBedrock(prompt);
        return SimilarProblemResponse.of(result);
    }

    private String invokeBedrock(String userMessage) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("anthropic_version", "bedrock-2023-05-31");
            requestBody.put("max_tokens", 2048);
            requestBody.put("system", SYSTEM_PROMPT);

            ArrayNode messages = requestBody.putArray("messages");
            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(MODEL_ID)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(objectMapper.writeValueAsString(requestBody)))
                    .build();

            InvokeModelResponse response = bedrockRuntimeClient.invokeModel(request);
            JsonNode responseJson = objectMapper.readTree(response.body().asUtf8String());

            return responseJson.path("content").get(0).path("text").asText();

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BEDROCK_INVOKE_FAIL);
        }
    }

    private String invokeBedrockClaude(List<ChatHistory> history, String newUserMessage) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("anthropic_version", "bedrock-2023-05-31");
            requestBody.put("max_tokens", 2048);
            requestBody.put("system", SYSTEM_PROMPT);

            ArrayNode messages = requestBody.putArray("messages");
            for (ChatHistory ch : history) {
                ObjectNode msg = messages.addObject();
                msg.put("role", ch.getRole());
                msg.put("content", ch.getMessage());
            }
            ObjectNode userMsg = messages.addObject();
            userMsg.put("role", "user");
            userMsg.put("content", newUserMessage);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(MODEL_ID)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(objectMapper.writeValueAsString(requestBody)))
                    .build();

            InvokeModelResponse response = bedrockRuntimeClient.invokeModel(request);
            JsonNode responseJson = objectMapper.readTree(response.body().asUtf8String());
            return responseJson.path("content").get(0).path("text").asText();

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BEDROCK_INVOKE_FAIL);
        }
    }
}
