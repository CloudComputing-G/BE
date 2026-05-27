package cloudproject.com.assignment.dto.response;

import cloudproject.com.assignment.domain.ChatHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponse {

    private Long chatId;
    private String role;
    private String message;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatHistory chatHistory) {
        ChatMessageResponse dto = new ChatMessageResponse();
        dto.chatId = chatHistory.getChatId();
        dto.role = chatHistory.getRole();
        dto.message = chatHistory.getMessage();
        dto.createdAt = chatHistory.getCreatedAt();
        return dto;
    }
}
