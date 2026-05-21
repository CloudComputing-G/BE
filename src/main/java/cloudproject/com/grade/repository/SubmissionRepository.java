package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
