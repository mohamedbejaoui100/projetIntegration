package com.edu3d.plateforme3d.repository;

import com.edu3d.plateforme3d.entity.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    List<ClassRoom> findByCourseTeacherId(Long teacherId);
    Optional<ClassRoom> findByInviteCode(String inviteCode);
    @Query("SELECT c FROM ClassRoom c WHERE c.course.id IN " +
            "(SELECT e.course.id FROM Enrollment e WHERE e.student.id = :studentId)")
    List<ClassRoom> findByEnrolledStudentId(@Param("studentId") Long studentId);
}
