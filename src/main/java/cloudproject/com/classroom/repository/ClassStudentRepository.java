package cloudproject.com.classroom.repository;

import cloudproject.com.classroom.domain.ClassStudent;
import cloudproject.com.classroom.domain.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    long countByClassroom_ClassId(Long classId);
    boolean existsByClassroom_ClassIdAndStudent_UserId(Long classId, Long studentId);

    @Query("SELECT cs.classroom FROM ClassStudent cs WHERE cs.student.userId = :studentId")
    List<Classroom> findClassroomsByStudentId(@Param("studentId") Long studentId);
}
