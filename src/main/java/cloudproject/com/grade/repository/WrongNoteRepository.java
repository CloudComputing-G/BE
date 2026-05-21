package cloudproject.com.grade.repository;

import cloudproject.com.grade.domain.WrongNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrongNoteRepository extends JpaRepository<WrongNote, Long> {
}
