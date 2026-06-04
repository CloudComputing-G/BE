package cloudproject.com.global.config;

import cloudproject.com.grade.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupInitializer {

    private final SubmissionRepository submissionRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void failStuckPendingSubmissions() {
        int count = submissionRepository.failAllPending("서버 재시작으로 인한 채점 실패");
        if (count > 0) {
            log.warn("서버 시작 시 PENDING 상태 제출물 {}건을 FAILED로 전환했습니다.", count);
        }
    }
}
