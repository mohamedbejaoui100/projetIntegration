package com.edu3d.plateforme3d.entity;
@Entity
@Table(name = "slides")
public class Slide {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentText;

    private String object3dUrl; // URL vers le fichier .glb / .gltf

    @Column(nullable = false)
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
