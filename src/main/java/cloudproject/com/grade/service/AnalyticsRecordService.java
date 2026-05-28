package cloudproject.com.grade.service;

import cloudproject.com.grade.dto.response.AssignmentAnalyticsResponse;
import cloudproject.com.grade.dto.response.WeakPointResponse;
import cloudproject.com.grade.repository.AnalyticsRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsRecordService {

    private final AnalyticsRecordRepository analyticsRecordRepository;

    public List<WeakPointResponse> getMyWeakPoints(Long studentId) {
        return analyticsRecordRepository.findByStudentIdOrderByPredictedErrorRateDesc(studentId)
                .stream()
                .map(WeakPointResponse::from)
                .collect(Collectors.toList());
    }

    public List<AssignmentAnalyticsResponse> getAssignmentAnalytics(Long assignmentId) {
        return analyticsRecordRepository.findByAssignmentIdWithStudent(assignmentId)
                .stream()
                .map(AssignmentAnalyticsResponse::from)
                .collect(Collectors.toList());
    }
}
