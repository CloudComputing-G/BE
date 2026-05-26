package cloudproject.com.classroom.repository;

import cloudproject.com.classroom.domain.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findByTeacher_UserId(Long teacherId);
}
