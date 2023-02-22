package uk.gov.cslearning.catalogue.dto.rustici.course;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ReferenceRequest {

    private final String webPathToCourse;
    private final String url;
}
