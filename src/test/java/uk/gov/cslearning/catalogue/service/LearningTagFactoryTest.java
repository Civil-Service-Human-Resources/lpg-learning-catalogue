package uk.gov.cslearning.catalogue.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.*;
import uk.gov.cslearning.catalogue.service.util.IUtilService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LearningTagFactoryTest {

    @Mock
    private IUtilService utilService;

    private LearningTagFactory learningTagFactory;

    private final LocalDateTime fixedNow = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

    @Before
    public void setUp() {
        learningTagFactory = new LearningTagFactory(utilService);
        when(utilService.getNowDateTime()).thenReturn(fixedNow);
    }

    @Test
    public void testCreateDtoWithoutParentAndEmptyRelations() {
        LearningTag tag = new LearningTag();
        tag.setId(1L);
        tag.setName("Tag Name");
        tag.setDescription("Tag Description");
        tag.setCode("TAG01");
        tag.setUrlSlug("tag-name");
        tag.setCategory(true);
        tag.setArchived(false);
        tag.setCreatedTimestamp(fixedNow);
        tag.setUpdatedTimestamp(fixedNow);
        tag.setArchivedTimestamp(null);

        LearningTagDto dto = learningTagFactory.createDto(tag);

        assertEquals(Long.valueOf(1L), dto.getId());
        assertEquals("Tag Name", dto.getName());
        assertEquals("Tag Description", dto.getDescription());
        assertEquals("TAG01", dto.getCode());
        assertEquals("tag-name", dto.getUrlSlug());
        assertTrue(dto.isCategory());
        assertFalse(dto.isArchived());
        assertNull(dto.getParentId());
        assertNull(dto.getParentName());
        assertEquals(fixedNow, dto.getCreatedTimestamp());
        assertEquals(fixedNow, dto.getUpdatedTimestamp());
        assertNull(dto.getArchivedTimestamp());
        assertEquals(Integer.valueOf(0), dto.getCourseCount());
        assertEquals(Integer.valueOf(0), dto.getLinkCount());
    }

    @Test
    public void testCreateDtoWithParentAndPopulatedRelations() {
        LearningTag parent = new LearningTag();
        parent.setId(10L);
        parent.setName("Parent Tag");

        LearningTag tag = new LearningTag();
        tag.setId(20L);
        tag.setName("Child Tag");
        tag.setDescription("Child Description");
        tag.setCode("CHILD01");
        tag.setUrlSlug("child-tag");
        tag.setCategory(false);
        tag.setArchived(true);
        tag.setParent(parent);
        tag.setCreatedTimestamp(fixedNow.minusDays(5));
        tag.setUpdatedTimestamp(fixedNow);
        tag.setArchivedTimestamp(fixedNow);

        CourseEntity course = new CourseEntity();
        course.setId(100L);
        CourseLearningTagEntity courseTag = new CourseLearningTagEntity(tag, course);
        tag.setCourses(new HashSet<>(Collections.singletonList(courseTag)));

        LearningTagHyperlink hyperlink = new LearningTagHyperlink(200L, tag, "https://example.com", "Example", "Desc", fixedNow, fixedNow);
        tag.setHyperlinks(new HashSet<>(Collections.singletonList(hyperlink)));

        LearningTagDto dto = learningTagFactory.createDto(tag);

        assertEquals(Long.valueOf(20L), dto.getId());
        assertEquals("Child Tag", dto.getName());
        assertEquals("Child Description", dto.getDescription());
        assertEquals("CHILD01", dto.getCode());
        assertEquals("child-tag", dto.getUrlSlug());
        assertFalse(dto.isCategory());
        assertTrue(dto.isArchived());
        assertEquals(Long.valueOf(10L), dto.getParentId());
        assertEquals("Parent Tag", dto.getParentName());
        assertEquals(Integer.valueOf(1), dto.getCourseCount());
        assertEquals(Integer.valueOf(1), dto.getLinkCount());
    }

    @Test
    public void testCreateLearningTagWithoutParent() {
        LearningTagDto dto = new LearningTagDto();
        dto.setName("New Tag");
        dto.setDescription("New Desc");
        dto.setCode("NEW01");
        dto.setUrlSlug("new-tag");
        dto.setCategory(true);
        dto.setArchived(false);

        LearningTag tag = learningTagFactory.create(dto);

        assertNull(tag.getId());
        assertEquals("New Tag", tag.getName());
        assertEquals("New Desc", tag.getDescription());
        assertEquals("NEW01", tag.getCode());
        assertEquals("new-tag", tag.getUrlSlug());
        assertTrue(tag.isCategory());
        assertFalse(tag.isArchived());
        assertNull(tag.getParent());
        assertEquals(fixedNow, tag.getCreatedTimestamp());
        assertEquals(fixedNow, tag.getUpdatedTimestamp());
    }

    @Test
    public void testCreateLearningTagWithParent() {
        LearningTag parent = new LearningTag();
        parent.setId(10L);
        parent.setName("Parent Tag");

        LearningTagDto dto = new LearningTagDto();
        dto.setName("New Tag");
        dto.setDescription("New Desc");
        dto.setCode("NEW01");
        dto.setUrlSlug("new-tag");
        dto.setCategory(false);
        dto.setArchived(false);

        LearningTag tag = learningTagFactory.create(dto, parent);

        assertEquals("New Tag", tag.getName());
        assertEquals(parent, tag.getParent());
        assertEquals(Long.valueOf(10L), tag.getParent().getId());
    }

    @Test
    public void testUpdateLearningTagWithoutParent() {
        LearningTag parent = new LearningTag();
        parent.setId(10L);

        LearningTag existingTag = new LearningTag();
        existingTag.setId(1L);
        existingTag.setName("Old Name");
        existingTag.setDescription("Old Desc");
        existingTag.setCode("OLD");
        existingTag.setUrlSlug("old-slug");
        existingTag.setCategory(false);
        existingTag.setArchived(false);
        existingTag.setParent(parent);

        LearningTagDto updateDto = new LearningTagDto();
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Desc");
        updateDto.setCode("UPDATED");
        updateDto.setUrlSlug("updated-slug");
        updateDto.setCategory(true);
        updateDto.setArchived(true);

        LearningTag updated = learningTagFactory.update(existingTag, updateDto);

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Desc", updated.getDescription());
        assertEquals("UPDATED", updated.getCode());
        assertEquals("updated-slug", updated.getUrlSlug());
        assertTrue(updated.isCategory());
        assertTrue(updated.isArchived());
        assertNull(updated.getParent());
        assertEquals(fixedNow, updated.getUpdatedTimestamp());
    }

    @Test
    public void testUpdateLearningTagWithParent() {
        LearningTag newParent = new LearningTag();
        newParent.setId(20L);
        newParent.setName("New Parent");

        LearningTag existingTag = new LearningTag();
        existingTag.setId(1L);

        LearningTagDto updateDto = new LearningTagDto();
        updateDto.setName("Child Name");
        updateDto.setDescription("Child Desc");
        updateDto.setCode("CHILD");
        updateDto.setUrlSlug("child-slug");
        updateDto.setCategory(false);
        updateDto.setArchived(false);

        LearningTag updated = learningTagFactory.update(existingTag, updateDto, newParent);

        assertEquals("Child Name", updated.getName());
        assertEquals(newParent, updated.getParent());
    }

    @Test
    public void testUpdateStateArchive() {
        LearningTag tag1 = new LearningTag();
        tag1.setId(1L);
        tag1.setArchived(false);

        List<LearningTag> result = learningTagFactory.updateState(Collections.singletonList(tag1), LearningTagState.ARCHIVE);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isArchived());
        assertEquals(fixedNow, result.get(0).getArchivedTimestamp());
        assertEquals(fixedNow, result.get(0).getUpdatedTimestamp());
    }

    @Test
    public void testUpdateStateUnarchive() {
        LearningTag tag1 = new LearningTag();
        tag1.setId(1L);
        tag1.setArchived(true);
        tag1.setArchivedTimestamp(fixedNow.minusDays(1));

        List<LearningTag> result = learningTagFactory.updateState(Collections.singletonList(tag1), LearningTagState.UNARCHIVE);

        assertEquals(1, result.size());
        assertFalse(result.get(0).isArchived());
        assertNull(result.get(0).getArchivedTimestamp());
        assertEquals(fixedNow, result.get(0).getUpdatedTimestamp());
    }

    @Test
    public void testHyperlinkOperations() {
        LearningTag tag = new LearningTag();
        tag.setId(1L);

        LearningTagHyperlink link = new LearningTagHyperlink(100L, tag, "https://test.com", "Test Title", "Test Desc", fixedNow, fixedNow);
        LearningTagHyperlinkDto dto = learningTagFactory.createHyperlinkDto(link);

        assertEquals(Long.valueOf(100L), dto.getId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Desc", dto.getDescription());
        assertEquals("https://test.com", dto.getUrl());

        LearningTagHyperlinkDto newDto = new LearningTagHyperlinkDto(null, "New Title", "New Desc", "https://new.com");
        LearningTagHyperlink created = learningTagFactory.createHyperlink(newDto, tag);

        assertNull(created.getId());
        assertEquals("New Title", created.getTitle());
        assertEquals("New Desc", created.getDescription());
        assertEquals("https://new.com", created.getHref());
        assertEquals(tag, created.getLearningTag());
        assertEquals(fixedNow, created.getCreatedTimestamp());
        assertEquals(fixedNow, created.getUpdatedTimestamp());

        LearningTagHyperlinkDto updateDto = new LearningTagHyperlinkDto(100L, "Updated Title", "Updated Desc", "https://updated.com");
        LearningTagHyperlink updated = learningTagFactory.updateHyperlink(link, updateDto);

        assertEquals(Long.valueOf(100L), updated.getId());
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Desc", updated.getDescription());
        assertEquals("https://updated.com", updated.getHref());
        assertEquals(fixedNow, updated.getUpdatedTimestamp());
    }
}
