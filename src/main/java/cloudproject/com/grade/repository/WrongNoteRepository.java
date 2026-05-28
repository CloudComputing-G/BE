package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.WrongNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WrongNoteRepository extends JpaRepository<WrongNote, Long> {

    @Query("SELECT wn FROM WrongNote wn " +
           "JOIN FETCH wn.result r " +
           "JOIN FETCH r.question q " +
           "JOIN FETCH q.assignment a " +
           "WHERE wn.student.userId = :studentId " +
           "ORDER BY wn.createdAt DESC")
    List<WrongNote> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    @Query("SELECT wn FROM WrongNote wn " +
           "JOIN FETCH wn.result r " +
           "JOIN FETCH r.question q " +
           "JOIN FETCH q.assignment a " +
           "WHERE wn.wrongNoteId = :wrongNoteId")
    Optional<WrongNote> findByIdWithDetails(@Param("wrongNoteId") Long wrongNoteId);
}
