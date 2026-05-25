package cloudproject.com.assignment.repository;

import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByClassroom_ClassIdAndTeacher(Long classId, User teacher);

    List<Assignment> findByClassroom_ClassIdAndStatus(Long classId, String status);

    @Query("""
            select a from Assignment a
            join fetch a.teacher
            where a.assignmentId = :assignmentId
            """)
    Optional<Assignment> findByIdWithTeacher(@Param("assignmentId") Long assignmentId);
}
