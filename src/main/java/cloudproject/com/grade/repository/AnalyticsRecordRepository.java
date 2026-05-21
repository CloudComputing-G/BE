package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.AnalyticsRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticsRecordRepository extends JpaRepository<AnalyticsRecord, Long> {
}
