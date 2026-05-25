package cloudproject.com.grade.service;

import cloudproject.com.grade.domain.Submission;
import cloudproject.com.grade.repository.SubmissionRepository;
import cloudproject.com.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cloudproject.com.global.common.code.ErrorCode.SUBMISSION_NOT_FOUND;
import static cloudproject.com.global.common.code.ErrorCode.SUBMISSION_NOT_GRADED;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradingService {

    private static final String GRADING_STATUS_PENDING = "PENDING";

    private final SubmissionRepository submissionRepository;

    @Transactional
    public void startGrading(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new BusinessException(SUBMISSION_NOT_FOUND));

        if (!GRADING_STATUS_PENDING.equals(submission.getGradingStatus())) {
            throw new BusinessException(SUBMISSION_NOT_GRADED);
        }

        // TODO: Lambda 채점 워커 invoke 메커니즘 미정. 합의 완료 후 구현.
        //   옵션 1) AWS Lambda async invoke (software.amazon.awssdk:lambda)
        //   옵션 2) SQS publish
        //   옵션 3) S3 event 사용 시 이 메서드 자체가 불필요
        log.info("Grading triggered for submissionId={} (Lambda invoke not yet wired)", submissionId);
    }
}
