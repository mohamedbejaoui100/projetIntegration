package com.edu3d.plateforme3d.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classrooms")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String inviteCode;   // ← code d'invitation unique

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @PrePersist
    public void generateCode() {
        if (this.inviteCode == null) {
            this.inviteCode = java.util.UUID.randomUUID()
                    .toString().substring(0, 8).toUpperCase();
        }
    }
}
