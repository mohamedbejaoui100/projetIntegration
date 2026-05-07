package com.edu3d.plateforme3d.repository;

import com.edu3d.plateforme3d.entity.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    List<ClassRoom> findByCourseTeacherId(Long teacherId);
    Optional<ClassRoom> findByInviteCode(String inviteCode);
}