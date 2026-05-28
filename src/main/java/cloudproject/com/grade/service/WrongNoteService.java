package cloudproject.com.grade.service;

import cloudproject.com.global.common.code.ErrorCode;
import cloudproject.com.global.error.BusinessException;
import cloudproject.com.grade.domain.WrongNote;
import cloudproject.com.grade.dto.response.WrongNoteResponse;
import cloudproject.com.grade.repository.WrongNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WrongNoteService {

    private final WrongNoteRepository wrongNoteRepository;

    public List<WrongNoteResponse> getMyWrongNotes(Long studentId) {
        return wrongNoteRepository.findByStudentIdWithDetails(studentId)
                .stream()
                .map(WrongNoteResponse::from)
                .collect(Collectors.toList());
    }

    public WrongNoteResponse getWrongNote(Long wrongNoteId, Long studentId) {
        WrongNote wrongNote = wrongNoteRepository.findByIdWithDetails(wrongNoteId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WRONG_NOTE_NOT_FOUND));

        if (!wrongNote.getStudent().getUserId().equals(studentId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return WrongNoteResponse.from(wrongNote);
    }
}
