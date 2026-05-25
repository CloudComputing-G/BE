package cloudproject.com.assignment.repository;

import cloudproject.com.assignment.domain.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("""
            select a from Assignment a
            join fetch a.teacher
            where a.assignmentId = :assignmentId
            """)
    Optional<Assignment> findByIdWithTeacher(@Param("assignmentId") Long assignmentId);
}
