package cloudproject.com.application;

import cloudproject.com.application.domain.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    // 반별 학생 수
    long countByClassEntity_ClassId(Long classId);
}
