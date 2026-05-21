package cloudproject.com.application.assignment.repository;

import cloudproject.com.application.domain.Assignment;
import cloudproject.com.application.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment,Long> {
    List<Assignment> findByClassEntity_ClassIdAndTeacher(Long classId, User teacher);
}
