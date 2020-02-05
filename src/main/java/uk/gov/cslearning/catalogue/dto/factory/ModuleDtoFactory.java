package uk.gov.cslearning.catalogue.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.dto.ModuleDto;

@Component
public class ModuleDtoFactory {

    private final CourseDtoFactory courseDtoFactory;

    public ModuleDtoFactory(CourseDtoFactory courseDtoFactory) {
        this.courseDtoFactory = courseDtoFactory;
    }

    public ModuleDto create(Module module, Course course) {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(module.getId());
        moduleDto.setTitle(module.getTitle());
        moduleDto.setType(module.getModuleType());
        moduleDto.setRequired(!module.isOptional());
        moduleDto.setCourse(courseDtoFactory.create(course));
        moduleDto.setAssociatedLearning(module.isAssociatedLearning());

        return moduleDto;
    }
}
