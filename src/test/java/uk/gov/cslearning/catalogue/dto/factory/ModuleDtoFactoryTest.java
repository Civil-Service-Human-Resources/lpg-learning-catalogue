package uk.gov.cslearning.catalogue.dto.factory;

import org.junit.Test;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.dto.ModuleDto;

import static org.junit.Assert.*;

public class ModuleDtoFactoryTest {
    private ModuleDtoFactory dtoFactory = new ModuleDtoFactory();

    @Test
    public void shouldReturnRequiredModuleDto() {
        String id = "module-id";
        String title = "module-title";

        FaceToFaceModule module = new FaceToFaceModule("abc");
        module.setOptional(false);
        module.setId(id);
        module.setTitle(title);

        ModuleDto dto = dtoFactory.create(module);
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertTrue(dto.isRequired());
    }

    @Test
    public void shouldReturnModuleDto() {
        String id = "module-id";
        String title = "module-title";

        FaceToFaceModule module = new FaceToFaceModule("abc");
        module.setOptional(true);
        module.setId(id);
        module.setTitle(title);

        ModuleDto dto = dtoFactory.create(module);
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertFalse(dto.isRequired());
    }
}