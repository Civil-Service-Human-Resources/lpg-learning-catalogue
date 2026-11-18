package uk.gov.cslearning.catalogue.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "course_levels")
@NoArgsConstructor
public class CourseLevelEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column
    private String description;

    public CourseLevelEntity(String name) {
        this.name = name;
    }
}
