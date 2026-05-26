package cloudproject.com.classroom.controller;

import cloudproject.com.classroom.dto.request.ClassroomCreateRequest;
import cloudproject.com.classroom.dto.request.ClassroomUpdateRequest;
import cloudproject.com.classroom.dto.response.ClassroomResponse;
import cloudproject.com.classroom.service.ClassroomService;
import cloudproject.com.global.common.code.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Classroom", description = "반(클래스) 관련 API")
@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    // ──────────────────────────────────────────────────
    // 반 생성
    // ──────────────────────────────────────────────────
    @Operation(
            summary = "반 생성",
            description = "선생님이 새로운 반을 생성합니다. **선생님 계정만 사용 가능합니다.**"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "반 생성 성공",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "입력값 오류 (name 누락 등)"),
            @ApiResponse(responseCode = "401", description = "인증 토큰 없음 또는 만료"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<cloudproject.com.global.common.ApiResponse<ClassroomResponse>> createClassroom(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "생성할 반 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClassroomCreateRequest.class))
            )
            @Valid @RequestBody ClassroomCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long teacherId
    ) {
        ClassroomResponse response = classroomService.createClassroom(request, teacherId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cloudproject.com.global.common.ApiResponse.success(SuccessCode.CLASSROOM_CREATED, response));
    }

    // ──────────────────────────────────────────────────
    // 반 목록 조회
    // ──────────────────────────────────────────────────
    @Operation(
            summary = "내 반 목록 조회",
            description = """
                    로그인한 사용자의 반 목록을 반환합니다.
                    - **선생님**: 본인이 담당하는 반 목록 (학생 수 포함)
                    - **학생**: 본인이 등록된 반 목록
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "반 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 토큰 없음 또는 만료")
    })
    @GetMapping
    public ResponseEntity<cloudproject.com.global.common.ApiResponse<List<ClassroomResponse>>> getClassrooms(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        List<ClassroomResponse> response = classroomService.getClassrooms(userId, isTeacher);
        return ResponseEntity.ok(cloudproject.com.global.common.ApiResponse.success(SuccessCode.CLASSROOMS_FETCHED, response));
    }

    // ──────────────────────────────────────────────────
    // 반 단건 조회
    // ──────────────────────────────────────────────────
    @Operation(
            summary = "반 단건 조회",
            description = """
                    특정 반의 상세 정보를 조회합니다.
                    - **선생님**: 본인이 담당하는 반만 조회 가능 (학생 수 포함)
                    - **학생**: 본인이 등록된 반만 조회 가능
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "반 조회 성공",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 토큰 없음 또는 만료"),
            @ApiResponse(responseCode = "403", description = "해당 반에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "반을 찾을 수 없음")
    })
    @GetMapping("/{classId}")
    public ResponseEntity<cloudproject.com.global.common.ApiResponse<ClassroomResponse>> getClassroom(
            @Parameter(description = "조회할 반 ID", required = true, example = "1")
            @PathVariable Long classId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(hidden = true) Authentication authentication
    ) {
        boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));
        ClassroomResponse response = classroomService.getClassroom(classId, userId, isTeacher);
        return ResponseEntity.ok(cloudproject.com.global.common.ApiResponse.success(SuccessCode.CLASSROOM_FETCHED, response));
    }

    // ──────────────────────────────────────────────────
    // 반 수정
    // ──────────────────────────────────────────────────
    @Operation(
            summary = "반 수정",
            description = "반 이름을 수정합니다. **해당 반을 생성한 선생님만 수정 가능합니다.**"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "반 수정 성공",
                    content = @Content(schema = @Schema(implementation = ClassroomResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "입력값 오류 (name 누락 등)"),
            @ApiResponse(responseCode = "401", description = "인증 토큰 없음 또는 만료"),
            @ApiResponse(responseCode = "403", description = "해당 반에 대한 수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "반을 찾을 수 없음")
    })
    @PutMapping("/{classId}")
    public ResponseEntity<cloudproject.com.global.common.ApiResponse<ClassroomResponse>> updateClassroom(
            @Parameter(description = "수정할 반 ID", required = true, example = "1")
            @PathVariable Long classId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 반 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ClassroomUpdateRequest.class))
            )
            @Valid @RequestBody ClassroomUpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long teacherId
    ) {
        ClassroomResponse response = classroomService.updateClassroom(classId, request, teacherId);
        return ResponseEntity.ok(cloudproject.com.global.common.ApiResponse.success(SuccessCode.CLASSROOM_UPDATED, response));
    }

    // ──────────────────────────────────────────────────
    // 반 삭제
    // ──────────────────────────────────────────────────
    @Operation(
            summary = "반 삭제",
            description = "반을 삭제합니다. **해당 반을 생성한 선생님만 삭제 가능합니다.**"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "반 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 토큰 없음 또는 만료"),
            @ApiResponse(responseCode = "403", description = "해당 반에 대한 삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "반을 찾을 수 없음")
    })
    @DeleteMapping("/{classId}")
    public ResponseEntity<cloudproject.com.global.common.ApiResponse<Void>> deleteClassroom(
            @Parameter(description = "삭제할 반 ID", required = true, example = "1")
            @PathVariable Long classId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long teacherId
    ) {
        classroomService.deleteClassroom(classId, teacherId);
        return ResponseEntity.ok(cloudproject.com.global.common.ApiResponse.success(SuccessCode.CLASSROOM_DELETED));
    }
}
