package com.edu3d.plateforme3d.repository;

import com.edu3d.plateforme3d.entity.Slide;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SlideRepository extends JpaRepository<Slide, Long> {
    List<Slide> findByCourseIdOrderByPositionAsc(Long courseId);
}
