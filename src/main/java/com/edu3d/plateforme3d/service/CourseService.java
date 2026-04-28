package com.edu3d.plateforme3d.service;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseResponse createCourse(CourseRequest request, Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable"));

        if (teacher.getRole() != Role.TEACHER && teacher.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Seuls les enseignants peuvent créer des cours");
        }

        Course course = new Course();
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setTeacher(teacher);

        Course saved = courseRepository.save(course);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable avec l'id: " + id));
        return mapToResponse(course);
    }

    private CourseResponse mapToResponse(Course c) {
        return new CourseResponse(
                c.getId(), c.getTitle(), c.getDescription(),
                c.getTeacher().getNom(), c.getSlides().size(), c.getCreatedAt()
        );
    }
}
