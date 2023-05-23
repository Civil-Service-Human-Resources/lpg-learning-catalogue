package uk.gov.cslearning.catalogue.dto.rustici.course;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateCourse {

    private final ReferenceRequest referenceRequest;

    public static CreateCourse createFromData (String webPathToCourse, String url) {
        ReferenceRequest request = new ReferenceRequest(webPathToCourse, url);
        return new CreateCourse(request);
    }
}
