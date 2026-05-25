package cloudproject.com.grade.service;

import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.assignment.repository.AssignmentRepository;
import cloudproject.com.assignment.repository.QuestionRepository;
import cloudproject.com.auth.domain.Role;
import cloudproject.com.grade.domain.GradingStatus;
import cloudproject.com.grade.domain.Submission;
import cloudproject.com.grade.dto.LeaderboardResponse;
import cloudproject.com.grade.repository.SubmissionRepository;
import cloudproject.com.grade.support.ScoreCalculator;
import cloudproject.com.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cloudproject.com.global.common.code.ErrorCode.ASSIGNMENT_ACCESS_DENIED;
import static cloudproject.com.global.common.code.ErrorCode.ASSIGNMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public LeaderboardResponse getLeaderboard(Long assignmentId, Long currentUserId, Role currentRole) {
        if (currentRole != Role.TEACHER) {
            throw new BusinessException(ASSIGNMENT_ACCESS_DENIED);
        }

        Assignment assignment = assignmentRepository.findByIdWithTeacher(assignmentId)
                .orElseThrow(() -> new BusinessException(ASSIGNMENT_NOT_FOUND));

        if (!assignment.getTeacher().getUserId().equals(currentUserId)) {
            throw new BusinessException(ASSIGNMENT_ACCESS_DENIED);
        }

        Long maxScoreSum = questionRepository.sumMaxScoreByAssignmentId(assignmentId);
        int maxScore = maxScoreSum == null ? 0 : Math.toIntExact(maxScoreSum);

        List<Submission> latestPerStudent = pickLatestPerStudent(
                submissionRepository.findAllByAssignmentIdWithStudent(assignmentId)
        );

        List<Submission> done = latestPerStudent.stream()
                .filter(s -> s.getGradingStatus() == GradingStatus.DONE)
                .sorted(Comparator.comparing(
                        Submission::getTotalScore,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();

        List<Submission> others = latestPerStudent.stream()
                .filter(s -> s.getGradingStatus() != GradingStatus.DONE)
                .sorted(Comparator.comparing(Submission::getSubmittedAt))
                .toList();

        List<LeaderboardResponse.RankingItem> rankings = new ArrayList<>();
        rankings.addAll(buildRankedItems(done, maxScore));
        rankings.addAll(buildUnrankedItems(others));

        return new LeaderboardResponse(
                assignmentId,
                assignment.getTitle(),
                maxScore,
                rankings
        );
    }

    private List<Submission> pickLatestPerStudent(List<Submission> submissionsOrderedDesc) {
        Map<Long, Submission> latestByStudent = new LinkedHashMap<>();
        for (Submission s : submissionsOrderedDesc) {
            latestByStudent.putIfAbsent(s.getStudent().getUserId(), s);
        }
        return new ArrayList<>(latestByStudent.values());
    }

    private List<LeaderboardResponse.RankingItem> buildRankedItems(List<Submission> done, int maxScore) {
        List<LeaderboardResponse.RankingItem> items = new ArrayList<>(done.size());
        Integer prevScore = null;
        int prevRank = 0;
        for (int i = 0; i < done.size(); i++) {
            Submission s = done.get(i);
            int rank;
            if (i == 0) {
                rank = 1;
            } else if (Objects.equals(s.getTotalScore(), prevScore)) {
                rank = prevRank;
            } else {
                rank = i + 1;
            }
            prevScore = s.getTotalScore();
            prevRank = rank;

            items.add(new LeaderboardResponse.RankingItem(
                    rank,
                    s.getStudent().getUserId(),
                    s.getStudent().getName(),
                    s.getTotalScore(),
                    ScoreCalculator.calculateCorrectRate(s.getTotalScore(), maxScore),
                    s.getGradingStatus(),
                    s.getSubmittedAt()
            ));
        }
        return items;
    }

    private List<LeaderboardResponse.RankingItem> buildUnrankedItems(List<Submission> others) {
        return others.stream()
                .map(s -> new LeaderboardResponse.RankingItem(
                        null,
                        s.getStudent().getUserId(),
                        s.getStudent().getName(),
                        null,
                        null,
                        s.getGradingStatus(),
                        s.getSubmittedAt()
                ))
                .toList();
    }

}
