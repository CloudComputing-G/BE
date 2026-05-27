package cloudproject.com.grade.controller;

import cloudproject.com.global.common.ApiResponse;
import cloudproject.com.global.common.code.SuccessCode;
import cloudproject.com.grade.dto.response.WrongNoteResponse;
import cloudproject.com.grade.service.WrongNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WrongNote", description = "오답노트 관련 API")
@RestController
@RequiredArgsConstructor
public class WrongNoteController {

    private final WrongNoteService wrongNoteService;

    @Operation(
            summary = "내 오답노트 목록 조회",
            description = "JWT 토큰에서 추출한 학생 ID로 본인의 오답노트 전체 목록을 조회합니다."
    )
    @GetMapping("/api/students/me/wrong-notes")
    public ResponseEntity<ApiResponse<List<WrongNoteResponse>>> getMyWrongNotes(
            @AuthenticationPrincipal Long studentId
    ) {
        List<WrongNoteResponse> response = wrongNoteService.getMyWrongNotes(studentId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.WRONG_NOTES_FETCHED, response));
    }

    @Operation(
            summary = "오답노트 상세 조회",
            description = "오답노트 ID로 특정 오답노트를 조회합니다. 본인 오답노트만 조회 가능합니다."
    )
    @GetMapping("/api/wrong-notes/{wrongNoteId}")
    public ResponseEntity<ApiResponse<WrongNoteResponse>> getWrongNote(
            @PathVariable Long wrongNoteId,
            @AuthenticationPrincipal Long studentId
    ) {
        WrongNoteResponse response = wrongNoteService.getWrongNote(wrongNoteId, studentId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.WRONG_NOTE_FETCHED, response));
    }
}
