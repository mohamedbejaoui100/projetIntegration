package com.edu3d.plateforme3d.repository;

public interface SlideRepository extends JpaRepository<Slide, Long> {
    List<Slide> findByCourseIdOrderByPositionAsc(Long courseId);
}
