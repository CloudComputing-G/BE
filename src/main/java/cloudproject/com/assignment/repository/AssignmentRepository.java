package cloudproject.com.assignment.repository;

import cloudproject.com.assignment.domain.Assignment;
import cloudproject.com.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment,Long> {
    List<Assignment> findByClassroom_ClassIdAndTeacher(Long classId, User teacher);
    List<Assignment> findByClassroom_ClassIdAndStatus(Long classId, String status);
}

