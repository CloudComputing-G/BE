package cloudproject.com.classroom.service;

import cloudproject.com.auth.domain.Role;
import cloudproject.com.auth.domain.User;
import cloudproject.com.auth.repository.UserRepository;
import cloudproject.com.classroom.domain.Classroom;
import cloudproject.com.classroom.domain.ClassStudent;
import cloudproject.com.classroom.dto.request.ClassroomCreateRequest;
import cloudproject.com.classroom.dto.request.ClassroomUpdateRequest;
import cloudproject.com.classroom.dto.request.ClassStudentAddRequest;
import cloudproject.com.classroom.dto.response.ClassroomResponse;
import cloudproject.com.classroom.dto.response.ClassStudentResponse;
import cloudproject.com.classroom.repository.ClassStudentRepository;
import cloudproject.com.classroom.repository.ClassroomRepository;
import cloudproject.com.global.common.code.ErrorCode;
import cloudproject.com.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassStudentRepository classStudentRepository;
    private final UserRepository userRepository;

    // 반 생성 (선생님만 가능)
    @Transactional
    public ClassroomResponse createClassroom(ClassroomCreateRequest request, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Classroom classroom = Classroom.builder()
                .teacher(teacher)
                .name(request.getName())
                .build();

        Classroom saved = classroomRepository.save(classroom);
        return ClassroomResponse.from(saved);
    }

    // 반 목록 조회
    // - 선생님: 본인이 담당하는 반 목록 (학생 수 포함)
    // - 학생: 본인이 등록된 반 목록
    public List<ClassroomResponse> getClassrooms(Long userId, boolean isTeacher) {
        if (isTeacher) {
            return classroomRepository.findByTeacher_UserId(userId)
                    .stream()
                    .map(classroom -> {
                        long studentCount = classStudentRepository.countByClassroom_ClassId(classroom.getClassId());
                        return ClassroomResponse.of(classroom, studentCount);
                    })
                    .collect(Collectors.toList());
        } else {
            return classStudentRepository.findClassroomsByStudentId(userId)
                    .stream()
                    .map(ClassroomResponse::from)
                    .collect(Collectors.toList());
        }
    }

    // 반 단건 조회
    public ClassroomResponse getClassroom(Long classId, Long userId, boolean isTeacher) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND));

        validateAccess(classroom, classId, userId, isTeacher);

        if (isTeacher) {
            long studentCount = classStudentRepository.countByClassroom_ClassId(classId);
            return ClassroomResponse.of(classroom, studentCount);
        }
        return ClassroomResponse.from(classroom);
    }

    // 반 수정 (선생님만 가능, 본인 반만)
    @Transactional
    public ClassroomResponse updateClassroom(Long classId, ClassroomUpdateRequest request, Long teacherId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND));

        if (!classroom.getTeacher().getUserId().equals(teacherId)) {
            throw new BusinessException(ErrorCode.CLASSROOM_FORBIDDEN);
        }

        classroom.update(request.getName());

        long studentCount = classStudentRepository.countByClassroom_ClassId(classId);
        return ClassroomResponse.of(classroom, studentCount);
    }

    // 반 삭제 (선생님만 가능, 본인 반만)
    @Transactional
    public void deleteClassroom(Long classId, Long teacherId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND));

        if (!classroom.getTeacher().getUserId().equals(teacherId)) {
            throw new BusinessException(ErrorCode.CLASSROOM_FORBIDDEN);
        }

        classroomRepository.delete(classroom);
    }

    // 학생 추가 (선생님만 가능, 본인 반만)
    // 이메일 목록으로 여러 학생을 한 번에 추가할 수 있음
    @Transactional
    public List<ClassStudentResponse> addStudents(Long classId, ClassStudentAddRequest request, Long teacherId) {
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASSROOM_NOT_FOUND));

        if (!classroom.getTeacher().getUserId().equals(teacherId)) {
            throw new BusinessException(ErrorCode.CLASSROOM_FORBIDDEN);
        }

        return request.getStudentEmails().stream()
                .map(email -> {
                    // 사용자 존재 여부 확인
                    User student = userRepository.findByEmail(email)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

                    // 학생 계정인지 확인
                    if (student.getRole() != Role.STUDENT) {
                        throw new BusinessException(ErrorCode.NOT_A_STUDENT);
                    }

                    // 이미 등록된 학생인지 확인
                    if (classStudentRepository.existsByClassroom_ClassIdAndStudent_UserId(classId, student.getUserId())) {
                        throw new BusinessException(ErrorCode.STUDENT_ALREADY_ENROLLED);
                    }

                    ClassStudent classStudent = ClassStudent.create(classroom, student);
                    return ClassStudentResponse.from(classStudentRepository.save(classStudent));
                })
                .collect(Collectors.toList());
    }

    // 접근 권한 확인 헬퍼
    private void validateAccess(Classroom classroom, Long classId, Long userId, boolean isTeacher) {
        if (isTeacher) {
            if (!classroom.getTeacher().getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.CLASSROOM_FORBIDDEN);
            }
        } else {
            boolean enrolled = classStudentRepository
                    .existsByClassroom_ClassIdAndStudent_UserId(classId, userId);
            if (!enrolled) {
                throw new BusinessException(ErrorCode.CLASSROOM_FORBIDDEN);
            }
        }
    }
}
