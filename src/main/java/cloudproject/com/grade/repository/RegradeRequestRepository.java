package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.RegradeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegradeRequestRepository extends JpaRepository<RegradeRequest, Long> {
}
