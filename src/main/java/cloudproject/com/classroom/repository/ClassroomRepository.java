package cloudproject.com.classroom.repository;

import cloudproject.com.classroom.domain.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
}
