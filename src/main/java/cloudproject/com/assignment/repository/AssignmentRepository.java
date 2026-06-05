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

    @Query("""
            select distinct a from Assignment a
            left join fetch a.questions
            where a.classroom.classId = :classId and a.teacher = :teacher
            """)
    List<Assignment> findByClassroom_ClassIdAndTeacher(@Param("classId") Long classId, @Param("teacher") User teacher);

    @Query("""
            select distinct a from Assignment a
            left join fetch a.questions
            where a.classroom.classId = :classId and a.status = :status
            """)
    List<Assignment> findByClassroom_ClassIdAndStatus(@Param("classId") Long classId, @Param("status") String status);

    @Query("""
            select a from Assignment a
            join fetch a.teacher
            where a.assignmentId = :assignmentId
            """)
    Optional<Assignment> findByIdWithTeacher(@Param("assignmentId") Long assignmentId);

    @Query("""
            select a from Assignment a
            join fetch a.teacher
            left join fetch a.questions
            where a.assignmentId = :assignmentId
            """)
    Optional<Assignment> findByIdWithTeacherAndQuestions(@Param("assignmentId") Long assignmentId);
}
