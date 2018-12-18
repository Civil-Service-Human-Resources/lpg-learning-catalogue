package uk.gov.cslearning.catalogue.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.dto.ModuleDto;

@Component
public class ModuleDtoFactory {

    public ModuleDto create(Module module) {
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(module.getId());
        moduleDto.setTitle(module.getTitle());
        moduleDto.setRequired(!module.isOptional());

        return moduleDto;
    }
}
