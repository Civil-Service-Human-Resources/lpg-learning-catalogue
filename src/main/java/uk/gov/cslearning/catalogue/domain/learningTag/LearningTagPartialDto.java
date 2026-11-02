package uk.gov.cslearning.catalogue.domain.learningTag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LearningTagPartialDto {
    private Long id;
    private String name;
    private String urlSlug;
}
