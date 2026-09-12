package uk.gov.cslearning.catalogue.domain;

import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LearningTagHyperlinkDto {

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String title;

    @Size(max = 255)
    private String description;

    @NotBlank
    @Size(max = 255)
    @URL(protocol = "https", message = "URL must be an HTTPS URL")
    private String url;
}
