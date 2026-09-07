package uk.gov.cslearning.catalogue.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LearningTagHyperlinkDto {

    private Long id;

    @NotBlank
    @Size(max = 50)
    private String title;

    @Size(max = 255)
    private String description;

    @NotBlank
    @Size(max = 255)
    @URL(protocol = "https", message = "url must be an HTTPS URL")
    private String url;
}
