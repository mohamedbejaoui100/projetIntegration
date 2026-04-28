package com.edu3d.plateforme3d.repository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTeacherId(Long teacherId);

    @Query("SELECT c FROM Course c JOIN c.enrollments e WHERE e.student.id = :studentId")
    List<Course> findEnrolledCoursesByStudentId(@Param("studentId") Long studentId);
}
