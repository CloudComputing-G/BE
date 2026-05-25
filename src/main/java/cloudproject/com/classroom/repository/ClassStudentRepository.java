package cloudproject.com.classroom.repository;

import cloudproject.com.classroom.domain.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    long countByClassroom_ClassId(Long classId);
    boolean existsByClassroom_ClassIdAndStudent_UserId(Long classId, Long studentId);
}
