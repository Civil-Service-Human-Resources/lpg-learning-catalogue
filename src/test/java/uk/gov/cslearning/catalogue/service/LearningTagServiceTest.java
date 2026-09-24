package uk.gov.cslearning.catalogue.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.api.models.*;
import uk.gov.cslearning.catalogue.domain.*;
import uk.gov.cslearning.catalogue.dto.BulkUpdateDto;
import uk.gov.cslearning.catalogue.exception.CustomValidationException;
import uk.gov.cslearning.catalogue.exception.ResourceNotFoundException;
import uk.gov.cslearning.catalogue.repository.elastic.CourseRepository;
import uk.gov.cslearning.catalogue.repository.sql.ICourseRepository;
import uk.gov.cslearning.catalogue.repository.sql.ICourseStatusRepository;
import uk.gov.cslearning.catalogue.repository.sql.ICourseTagRepository;
import uk.gov.cslearning.catalogue.repository.sql.ILearningTagHyperlinkRepository;
import uk.gov.cslearning.catalogue.repository.sql.ILearningTagRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LearningTagServiceTest {

    @Mock
    private ILearningTagRepository learningTagRepository;

    @Mock
    private ICourseTagRepository courseTagRepository;

    @Mock
    private ICourseRepository courseRepository;

    @Mock
    private ICourseStatusRepository courseStatusRepository;

    @Mock
    private LearningTagFactory learningTagFactory;

    @Mock
    private CourseRepository elasticCourseRepository;

    @Mock
    private ILearningTagHyperlinkRepository learningTagHyperlinkRepository;

    private LearningTagService learningTagService;

    @Before
    public void setUp() {
        learningTagService = new LearningTagService(
                learningTagRepository,
                courseTagRepository,
                courseRepository,
                courseStatusRepository,
                learningTagFactory,
                elasticCourseRepository,
                learningTagHyperlinkRepository
        );
    }

    @Test
    public void testGetHyperlinksByLearningTagId() {
        Long tagId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        LearningTag tag = new LearningTag();
        tag.setId(tagId);

        LearningTagHyperlink hyperlink = new LearningTagHyperlink(100L, tag, "https://bbc.co.uk", "BBC", "BBC Desc", null, null);
        List<LearningTagHyperlink> hyperlinks = Collections.singletonList(hyperlink);
        Page<LearningTagHyperlink> page = new PageImpl<>(hyperlinks, pageable, 1);

        LearningTagHyperlinkDto dto = new LearningTagHyperlinkDto(100L, "BBC", "BBC Desc", "https://bbc.co.uk");

        when(learningTagHyperlinkRepository.findByLearningTagIdOrderByTitleAsc(tagId, pageable)).thenReturn(page);
        when(learningTagFactory.createHyperlinkDto(hyperlink)).thenReturn(dto);

        SimplePage<LearningTagHyperlinkDto> result = learningTagService.getHyperlinksByLearningTagId(tagId, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(Long.valueOf(100L), result.getContent().get(0).getId());
        assertEquals("BBC", result.getContent().get(0).getTitle());
        assertEquals("BBC Desc", result.getContent().get(0).getDescription());
        assertEquals("https://bbc.co.uk", result.getContent().get(0).getUrl());
        assertEquals(1, result.getTotalResults());

        verify(learningTagHyperlinkRepository).findByLearningTagIdOrderByTitleAsc(tagId, pageable);
        verify(learningTagFactory).createHyperlinkDto(hyperlink);
    }

    @Test
    public void testCreateLearningTagHyperlink() throws Exception {
        Long tagId = 1L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);

        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(null, "BBC", "BBC Desc", "https://bbc.co.uk");
        LearningTagHyperlink createdEntity = new LearningTagHyperlink(100L, tag, "https://bbc.co.uk", "BBC", "BBC Desc", null, null);
        LearningTagHyperlinkDto expectedResultDto = new LearningTagHyperlinkDto(100L, "BBC", "BBC Desc", "https://bbc.co.uk");

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndTitle(tagId, "BBC")).thenReturn(false);
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndHref(tagId, "https://bbc.co.uk")).thenReturn(false);
        when(learningTagFactory.createHyperlink(inputDto, tag)).thenReturn(createdEntity);
        when(learningTagHyperlinkRepository.save(createdEntity)).thenReturn(createdEntity);
        when(learningTagFactory.createHyperlinkDto(createdEntity)).thenReturn(expectedResultDto);

        LearningTagHyperlinkDto result = learningTagService.createLearningTagHyperlink(tagId, inputDto);

        assertEquals(Long.valueOf(100L), result.getId());
        assertEquals("BBC", result.getTitle());
        assertEquals("BBC Desc", result.getDescription());
        assertEquals("https://bbc.co.uk", result.getUrl());

        verify(learningTagRepository).findById(tagId);
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndTitle(tagId, "BBC");
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndHref(tagId, "https://bbc.co.uk");
        verify(learningTagFactory).createHyperlink(inputDto, tag);
        verify(learningTagHyperlinkRepository).save(createdEntity);
        verify(learningTagFactory).createHyperlinkDto(createdEntity);
    }

    @Test
    public void testCreateLearningTagHyperlinkWhenTitleAlreadyExists() {
        Long tagId = 1L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);
        tag.setName("Tag1");

        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(null, "BBC", "BBC Desc", "https://bbc.co.uk");

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndTitle(tagId, "BBC")).thenReturn(true);
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndHref(tagId, "https://bbc.co.uk")).thenReturn(false);

        try {
            learningTagService.createLearningTagHyperlink(tagId, inputDto);
            fail("Expected CustomValidationException to be thrown");
        } catch (CustomValidationException e) {
            List<String> errors = e.getErrors();
            assertEquals(1, errors.size());
            assertEquals("Field title is invalid: A link with this title already exists for the tag", errors.get(0));
        }

        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndTitle(tagId, "BBC");
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndHref(tagId, "https://bbc.co.uk");
    }

    @Test
    public void testCreateLearningTagHyperlinkWhenHrefAlreadyExists() {
        Long tagId = 1L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);
        tag.setName("Tag1");

        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(null, "BBC", "BBC Desc", "https://bbc.co.uk");

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndTitle(tagId, "BBC")).thenReturn(false);
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndHref(tagId, "https://bbc.co.uk")).thenReturn(true);

        try {
            learningTagService.createLearningTagHyperlink(tagId, inputDto);
            fail("Expected CustomValidationException to be thrown");
        } catch (CustomValidationException e) {
            List<String> errors = e.getErrors();
            assertEquals(1, errors.size());
            assertEquals("Field url is invalid: A link with this URL already exists for the tag", errors.get(0));
        }

        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndTitle(tagId, "BBC");
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndHref(tagId, "https://bbc.co.uk");
    }

    @Test
    public void testCreateLearningTagHyperlinkWhenBothTitleAndHrefAlreadyExist() {
        Long tagId = 1L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);
        tag.setName("Tag1");

        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(null, "BBC", "BBC Desc", "https://bbc.co.uk");

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndTitle(tagId, "BBC")).thenReturn(true);
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndHref(tagId, "https://bbc.co.uk")).thenReturn(true);

        try {
            learningTagService.createLearningTagHyperlink(tagId, inputDto);
            fail("Expected CustomValidationException to be thrown");
        } catch (CustomValidationException e) {
            List<String> errors = e.getErrors();
            assertEquals(2, errors.size());
            assertTrue(errors.contains("Field title is invalid: A link with this title already exists for the tag"));
            assertTrue(errors.contains("Field url is invalid: A link with this URL already exists for the tag"));
        }

        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndTitle(tagId, "BBC");
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndHref(tagId, "https://bbc.co.uk");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateLearningTagHyperlinkWhenTagNotFound() throws Exception {
        Long tagId = 999L;
        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(null, "BBC", "BBC Desc", "https://bbc.co.uk");

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.empty());

        learningTagService.createLearningTagHyperlink(tagId, inputDto);
    }

    @Test
    public void testUpdateLearningTagHyperlink() throws Exception {
        Long tagId = 1L;
        Long hyperlinkId = 100L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);
        tag.setName("Tag1");

        LearningTagHyperlink existingEntity = new LearningTagHyperlink(hyperlinkId, tag, "https://old.co.uk", "Old Title", "Old Desc", null, null);
        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(hyperlinkId, "New Title", "New Desc", "https://new.co.uk");
        LearningTagHyperlink updatedEntity = new LearningTagHyperlink(hyperlinkId, tag, "https://new.co.uk", "New Title", "New Desc", null, null);
        LearningTagHyperlinkDto expectedResultDto = new LearningTagHyperlinkDto(hyperlinkId, "New Title", "New Desc", "https://new.co.uk");

        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(hyperlinkId, tagId)).thenReturn(Optional.of(existingEntity));
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndTitleAndIdNot(tagId, "New Title", hyperlinkId)).thenReturn(false);
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndHrefAndIdNot(tagId, "https://new.co.uk", hyperlinkId)).thenReturn(false);
        when(learningTagFactory.updateHyperlink(existingEntity, inputDto)).thenReturn(updatedEntity);
        when(learningTagHyperlinkRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(learningTagFactory.createHyperlinkDto(updatedEntity)).thenReturn(expectedResultDto);

        LearningTagHyperlinkDto result = learningTagService.updateLearningTagHyperlink(tagId, hyperlinkId, inputDto);

        assertEquals(Long.valueOf(100L), result.getId());
        assertEquals("New Title", result.getTitle());
        assertEquals("New Desc", result.getDescription());
        assertEquals("https://new.co.uk", result.getUrl());

        verify(learningTagHyperlinkRepository).findByIdAndLearningTagId(hyperlinkId, tagId);
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndTitleAndIdNot(tagId, "New Title", hyperlinkId);
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndHrefAndIdNot(tagId, "https://new.co.uk", hyperlinkId);
        verify(learningTagFactory).updateHyperlink(existingEntity, inputDto);
        verify(learningTagHyperlinkRepository).save(updatedEntity);
        verify(learningTagFactory).createHyperlinkDto(updatedEntity);
    }

    @Test
    public void testUpdateLearningTagHyperlinkWhenTitleAlreadyExists() {
        Long tagId = 1L;
        Long hyperlinkId = 100L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);
        tag.setName("Tag1");

        LearningTagHyperlink existingEntity = new LearningTagHyperlink(hyperlinkId, tag, "https://old.co.uk", "Old Title", "Old Desc", null, null);
        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(hyperlinkId, "New Title", "New Desc", "https://new.co.uk");

        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(hyperlinkId, tagId)).thenReturn(Optional.of(existingEntity));
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndTitleAndIdNot(tagId, "New Title", hyperlinkId)).thenReturn(true);
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndHrefAndIdNot(tagId, "https://new.co.uk", hyperlinkId)).thenReturn(false);

        try {
            learningTagService.updateLearningTagHyperlink(tagId, hyperlinkId, inputDto);
            fail("Expected CustomValidationException to be thrown");
        } catch (CustomValidationException e) {
            List<String> errors = e.getErrors();
            assertEquals(1, errors.size());
            assertEquals("Field title is invalid: A link with this title already exists for the tag", errors.get(0));
        }

        verify(learningTagHyperlinkRepository).findByIdAndLearningTagId(hyperlinkId, tagId);
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndTitleAndIdNot(tagId, "New Title", hyperlinkId);
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndHrefAndIdNot(tagId, "https://new.co.uk", hyperlinkId);
    }

    @Test
    public void testUpdateLearningTagHyperlinkWhenHrefAlreadyExists() {
        Long tagId = 1L;
        Long hyperlinkId = 100L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);
        tag.setName("Tag1");

        LearningTagHyperlink existingEntity = new LearningTagHyperlink(hyperlinkId, tag, "https://old.co.uk", "Old Title", "Old Desc", null, null);
        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(hyperlinkId, "New Title", "New Desc", "https://new.co.uk");

        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(hyperlinkId, tagId)).thenReturn(Optional.of(existingEntity));
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndTitleAndIdNot(tagId, "New Title", hyperlinkId)).thenReturn(false);
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndHrefAndIdNot(tagId, "https://new.co.uk", hyperlinkId)).thenReturn(true);

        try {
            learningTagService.updateLearningTagHyperlink(tagId, hyperlinkId, inputDto);
            fail("Expected CustomValidationException to be thrown");
        } catch (CustomValidationException e) {
            List<String> errors = e.getErrors();
            assertEquals(1, errors.size());
            assertEquals("Field url is invalid: A link with this URL already exists for the tag", errors.get(0));
        }

        verify(learningTagHyperlinkRepository).findByIdAndLearningTagId(hyperlinkId, tagId);
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndTitleAndIdNot(tagId, "New Title", hyperlinkId);
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndHrefAndIdNot(tagId, "https://new.co.uk", hyperlinkId);
    }

    @Test
    public void testUpdateLearningTagHyperlinkWhenBothTitleAndHrefAlreadyExist() {
        Long tagId = 1L;
        Long hyperlinkId = 100L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);
        tag.setName("Tag1");

        LearningTagHyperlink existingEntity = new LearningTagHyperlink(hyperlinkId, tag, "https://old.co.uk", "Old Title", "Old Desc", null, null);
        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(hyperlinkId, "New Title", "New Desc", "https://new.co.uk");

        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(hyperlinkId, tagId)).thenReturn(Optional.of(existingEntity));
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndTitleAndIdNot(tagId, "New Title", hyperlinkId)).thenReturn(true);
        when(learningTagHyperlinkRepository.existsByLearningTagIdAndHrefAndIdNot(tagId, "https://new.co.uk", hyperlinkId)).thenReturn(true);

        try {
            learningTagService.updateLearningTagHyperlink(tagId, hyperlinkId, inputDto);
            fail("Expected CustomValidationException to be thrown");
        } catch (CustomValidationException e) {
            List<String> errors = e.getErrors();
            assertEquals(2, errors.size());
            assertTrue(errors.contains("Field title is invalid: A link with this title already exists for the tag"));
            assertTrue(errors.contains("Field url is invalid: A link with this URL already exists for the tag"));
        }

        verify(learningTagHyperlinkRepository).findByIdAndLearningTagId(hyperlinkId, tagId);
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndTitleAndIdNot(tagId, "New Title", hyperlinkId);
        verify(learningTagHyperlinkRepository).existsByLearningTagIdAndHrefAndIdNot(tagId, "https://new.co.uk", hyperlinkId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateLearningTagHyperlinkWhenNotFound() throws Exception {
        Long tagId = 1L;
        Long hyperlinkId = 999L;
        LearningTagHyperlinkDto inputDto = new LearningTagHyperlinkDto(hyperlinkId, "New Title", "New Desc", "https://new.co.uk");

        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(hyperlinkId, tagId)).thenReturn(Optional.empty());

        learningTagService.updateLearningTagHyperlink(tagId, hyperlinkId, inputDto);
    }

    @Test
    public void testRemoveHyperlinksFromLearningTag() {
        Long tagId = 1L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        LearningTagHyperlink hyperlink1 = new LearningTagHyperlink(101L, tag, "https://bbc.co.uk", "BBC", "BBC Desc", null, null);
        LearningTagHyperlink hyperlink2 = new LearningTagHyperlink(102L, tag, "https://sky.com", "Sky", "Sky Desc", null, null);

        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(101L, tagId)).thenReturn(Optional.of(hyperlink1));
        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(102L, tagId)).thenReturn(Optional.of(hyperlink2));
        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(103L, tagId)).thenReturn(Optional.empty());

        IdsDto<Long> dto = new IdsDto<>(Arrays.asList(101L, 102L, 103L));
        BulkUpdateResponse<Long> response = learningTagService.removeHyperlinksFromLearningTag(tagId, dto);

        assertEquals(2, response.getSuccessfulIds().size());
        assertTrue(response.getSuccessfulIds().contains(101L));
        assertTrue(response.getSuccessfulIds().contains(102L));
        assertEquals(1, response.getFailedIds().size());
        assertTrue(response.getFailedIds().contains(103L));

        verify(learningTagHyperlinkRepository).delete(hyperlink1);
        verify(learningTagHyperlinkRepository).delete(hyperlink2);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testRemoveHyperlinksFromLearningTagWhenTagNotFound() {
        Long tagId = 999L;
        when(learningTagRepository.findById(tagId)).thenReturn(Optional.empty());

        IdsDto<Long> dto = new IdsDto<>(Arrays.asList(101L));
        learningTagService.removeHyperlinksFromLearningTag(tagId, dto);
    }

    @Test
    public void testRemoveCoursesFromLearningTag() {
        Long tagId = 1L;
        LearningTag tag = new LearningTag();
        tag.setId(tagId);

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        CourseEntity course1 = new CourseEntity();
        course1.setId(10L);
        course1.setUid("course-1");

        CourseEntity course2 = new CourseEntity();
        course2.setId(20L);
        course2.setUid("course-2");

        when(courseRepository.findByUid("course-1")).thenReturn(Optional.of(course1));
        when(courseRepository.findByUid("course-2")).thenReturn(Optional.of(course2));
        when(courseRepository.findByUid("course-3")).thenReturn(Optional.empty());

        when(courseTagRepository.existsById(new CourseLearningTagId(tagId, 10L))).thenReturn(true);
        when(courseTagRepository.existsById(new CourseLearningTagId(tagId, 20L))).thenReturn(false);

        IdsDto<String> dto = new IdsDto<>(Arrays.asList("course-1", "course-2", "course-3"));
        BulkUpdateResponse<String> response = learningTagService.removeCoursesFromLearningTag(tagId, dto);

        assertEquals(1, response.getSuccessfulIds().size());
        assertTrue(response.getSuccessfulIds().contains("course-1"));
        assertEquals(2, response.getFailedIds().size());
        assertTrue(response.getFailedIds().contains("course-2"));
        assertTrue(response.getFailedIds().contains("course-3"));

        verify(courseTagRepository).deleteById(new CourseLearningTagId(tagId, 10L));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testRemoveCoursesFromLearningTagWhenTagNotFound() {
        Long tagId = 999L;
        when(learningTagRepository.findById(tagId)).thenReturn(Optional.empty());

        IdsDto<String> dto = new IdsDto<>(Arrays.asList("course-1"));
        learningTagService.removeCoursesFromLearningTag(tagId, dto);
    }

    @Test
    public void testGetLearningTags() {
        Pageable pageable = PageRequest.of(0, 10);
        LearningTag tag1 = new LearningTag();
        tag1.setId(1L);
        tag1.setName("Tag1");

        LearningTagDto dto1 = new LearningTagDto();
        dto1.setId(1L);
        dto1.setName("Tag1");
        dto1.setCategory(true);
        dto1.setArchived(false);
        dto1.setCourseCount(3);
        dto1.setLinkCount(2);

        Page<LearningTag> page = new PageImpl<>(Collections.singletonList(tag1), pageable, 1);

        when(learningTagRepository.findAll(pageable)).thenReturn(page);
        when(learningTagFactory.createDto(tag1)).thenReturn(dto1);

        SimplePage<LearningTagDto> result = learningTagService.getLearningTags(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(Long.valueOf(1L), result.getContent().get(0).getId());
        assertEquals("Tag1", result.getContent().get(0).getName());
        assertTrue(result.getContent().get(0).isCategory());
        assertFalse(result.getContent().get(0).isArchived());
        assertEquals(Integer.valueOf(3), result.getContent().get(0).getCourseCount());
        assertEquals(Integer.valueOf(2), result.getContent().get(0).getLinkCount());
        assertEquals(1, result.getTotalResults());

        verify(learningTagRepository).findAll(pageable);
        verify(learningTagFactory).createDto(tag1);
    }

    @Test
    public void testGetCoursesByLearningTagId() {
        Long tagId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        CourseStatusEntity status = new CourseStatusEntity();
        status.setName("Published");

        CourseEntity course = new CourseEntity();
        course.setId(10L);
        course.setUid("course-abc");
        course.setTitle("Course ABC");
        course.setShortDescription("Short desc");
        course.setStatus(status);

        LearningTag tag = new LearningTag();
        tag.setId(tagId);

        CourseLearningTagEntity entity = new CourseLearningTagEntity(tag, course);
        Page<CourseLearningTagEntity> page = new PageImpl<>(Collections.singletonList(entity), pageable, 1);

        when(courseTagRepository.findByLearningTagIdOrderByCourseTitleAsc(tagId, pageable)).thenReturn(page);

        SimplePage<CourseLearningTagResponse> result = learningTagService.getCoursesByLearningTagId(tagId, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("course-abc", result.getContent().get(0).getId());
        assertEquals("Course ABC", result.getContent().get(0).getTitle());
        assertEquals("Short desc", result.getContent().get(0).getShortDescription());
        assertEquals("Published", result.getContent().get(0).getStatus());
        assertEquals(1, result.getTotalResults());

        verify(courseTagRepository).findByLearningTagIdOrderByCourseTitleAsc(tagId, pageable);
    }

    @Test
    public void testCreateLearningTagWithoutParent() {
        LearningTagDto inputDto = new LearningTagDto();
        inputDto.setName("New Tag");
        inputDto.setDescription("Desc");
        inputDto.setCode("NEW");
        inputDto.setUrlSlug("new-tag");
        inputDto.setCategory(true);

        LearningTag tagEntity = new LearningTag();
        tagEntity.setId(10L);
        tagEntity.setName("New Tag");

        LearningTagDto expectedDto = new LearningTagDto();
        expectedDto.setId(10L);
        expectedDto.setName("New Tag");
        expectedDto.setCategory(true);
        expectedDto.setCourseCount(0);
        expectedDto.setLinkCount(0);

        when(learningTagFactory.create(inputDto)).thenReturn(tagEntity);
        when(learningTagRepository.save(tagEntity)).thenReturn(tagEntity);
        when(learningTagFactory.createDto(tagEntity)).thenReturn(expectedDto);

        LearningTagDto result = learningTagService.createLearningTag(inputDto);

        assertEquals(Long.valueOf(10L), result.getId());
        assertEquals("New Tag", result.getName());
        assertTrue(result.isCategory());

        verify(learningTagFactory).create(inputDto);
        verify(learningTagRepository).save(tagEntity);
        verify(learningTagFactory).createDto(tagEntity);
    }

    @Test
    public void testCreateLearningTagWithParent() {
        LearningTagDto inputDto = new LearningTagDto();
        inputDto.setName("Child Tag");
        inputDto.setParentId(1L);

        LearningTag parentTag = new LearningTag();
        parentTag.setId(1L);
        parentTag.setName("Parent Tag");

        LearningTag childTag = new LearningTag();
        childTag.setId(2L);
        childTag.setName("Child Tag");
        childTag.setParent(parentTag);

        LearningTagDto expectedDto = new LearningTagDto();
        expectedDto.setId(2L);
        expectedDto.setName("Child Tag");
        expectedDto.setParentId(1L);
        expectedDto.setParentName("Parent Tag");

        when(learningTagRepository.findById(1L)).thenReturn(Optional.of(parentTag));
        when(learningTagFactory.create(inputDto, parentTag)).thenReturn(childTag);
        when(learningTagRepository.save(childTag)).thenReturn(childTag);
        when(learningTagFactory.createDto(childTag)).thenReturn(expectedDto);

        LearningTagDto result = learningTagService.createLearningTag(inputDto);

        assertEquals(Long.valueOf(2L), result.getId());
        assertEquals(Long.valueOf(1L), result.getParentId());
        assertEquals("Parent Tag", result.getParentName());

        verify(learningTagRepository).findById(1L);
        verify(learningTagFactory).create(inputDto, parentTag);
        verify(learningTagRepository).save(childTag);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateLearningTagWithNonExistentParent() {
        LearningTagDto inputDto = new LearningTagDto();
        inputDto.setName("Child Tag");
        inputDto.setParentId(999L);

        when(learningTagRepository.findById(999L)).thenReturn(Optional.empty());

        learningTagService.createLearningTag(inputDto);
    }

    @Test
    public void testUpdateLearningTagWithoutParent() {
        Long tagId = 1L;
        LearningTagDto inputDto = new LearningTagDto();
        inputDto.setName("Updated Tag");
        inputDto.setParentId(null);

        LearningTag existingTag = new LearningTag();
        existingTag.setId(tagId);

        LearningTag updatedTag = new LearningTag();
        updatedTag.setId(tagId);
        updatedTag.setName("Updated Tag");

        LearningTagDto expectedDto = new LearningTagDto();
        expectedDto.setId(tagId);
        expectedDto.setName("Updated Tag");

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
        when(learningTagFactory.update(existingTag, inputDto)).thenReturn(updatedTag);
        when(learningTagRepository.save(updatedTag)).thenReturn(updatedTag);
        when(learningTagFactory.createDto(updatedTag)).thenReturn(expectedDto);

        LearningTagDto result = learningTagService.updateLearningTag(tagId, inputDto);

        assertEquals(Long.valueOf(tagId), result.getId());
        assertEquals("Updated Tag", result.getName());

        verify(learningTagRepository).findById(tagId);
        verify(learningTagFactory).update(existingTag, inputDto);
        verify(learningTagRepository).save(updatedTag);
    }

    @Test
    public void testUpdateLearningTagWithParent() {
        Long tagId = 2L;
        LearningTagDto inputDto = new LearningTagDto();
        inputDto.setName("Child Tag");
        inputDto.setParentId(1L);

        LearningTag existingTag = new LearningTag();
        existingTag.setId(tagId);

        LearningTag parentTag = new LearningTag();
        parentTag.setId(1L);
        parentTag.setName("Parent Tag");

        LearningTag updatedTag = new LearningTag();
        updatedTag.setId(tagId);
        updatedTag.setName("Child Tag");
        updatedTag.setParent(parentTag);

        LearningTagDto expectedDto = new LearningTagDto();
        expectedDto.setId(tagId);
        expectedDto.setName("Child Tag");
        expectedDto.setParentId(1L);
        expectedDto.setParentName("Parent Tag");

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
        when(learningTagRepository.findById(1L)).thenReturn(Optional.of(parentTag));
        when(learningTagFactory.update(existingTag, inputDto, parentTag)).thenReturn(updatedTag);
        when(learningTagRepository.save(updatedTag)).thenReturn(updatedTag);
        when(learningTagFactory.createDto(updatedTag)).thenReturn(expectedDto);

        LearningTagDto result = learningTagService.updateLearningTag(tagId, inputDto);

        assertEquals(Long.valueOf(tagId), result.getId());
        assertEquals(Long.valueOf(1L), result.getParentId());
        assertEquals("Parent Tag", result.getParentName());

        verify(learningTagRepository).findById(tagId);
        verify(learningTagRepository).findById(1L);
        verify(learningTagFactory).update(existingTag, inputDto, parentTag);
        verify(learningTagRepository).save(updatedTag);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateLearningTagWhenTagNotFound() {
        Long tagId = 999L;
        LearningTagDto inputDto = new LearningTagDto();
        when(learningTagRepository.findById(tagId)).thenReturn(Optional.empty());

        learningTagService.updateLearningTag(tagId, inputDto);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdateLearningTagWhenParentNotFound() {
        Long tagId = 2L;
        LearningTagDto inputDto = new LearningTagDto();
        inputDto.setParentId(999L);

        LearningTag existingTag = new LearningTag();
        existingTag.setId(tagId);

        when(learningTagRepository.findById(tagId)).thenReturn(Optional.of(existingTag));
        when(learningTagRepository.findById(999L)).thenReturn(Optional.empty());

        learningTagService.updateLearningTag(tagId, inputDto);
    }

    @Test
    public void testUpdateLearningTagState() {
        LearningTag tag1 = new LearningTag();
        tag1.setId(1L);
        LearningTag tag2 = new LearningTag();
        tag2.setId(2L);

        List<LearningTag> tags = Arrays.asList(tag1, tag2);
        LearningTagBulkStateDto dto = new LearningTagBulkStateDto(Arrays.asList(1L, 2L), LearningTagState.ARCHIVE);

        when(learningTagRepository.findAllById(dto.getIds())).thenReturn(tags);
        when(learningTagFactory.updateState(tags, LearningTagState.ARCHIVE)).thenReturn(tags);
        when(learningTagRepository.save(tag1)).thenReturn(tag1);
        when(learningTagRepository.save(tag2)).thenThrow(new RuntimeException("DB error"));

        BulkUpdateDto result = learningTagService.updateLearningTagState(dto);

        assertEquals(1, result.getSuccessfulUpdates().size());
        assertTrue(result.getSuccessfulUpdates().contains(1L));
        assertEquals(1, result.getFailedUpdates().size());
        assertTrue(result.getFailedUpdates().contains(2L));

        verify(learningTagRepository).findAllById(dto.getIds());
        verify(learningTagFactory).updateState(tags, LearningTagState.ARCHIVE);
    }

    @Test
    public void testGetLearningTagByIdSuccess() {
        LearningTag tag = new LearningTag();
        tag.setId(1L);
        tag.setName("Tag1");

        when(learningTagRepository.findById(1L)).thenReturn(Optional.of(tag));

        LearningTag result = learningTagService.getLearningTagById(1L);

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getId());
        assertEquals("Tag1", result.getName());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetLearningTagByIdNotFound() {
        when(learningTagRepository.findById(999L)).thenReturn(Optional.empty());
        learningTagService.getLearningTagById(999L);
    }

    @Test
    public void testAssignCoursesToTag() {
        LearningTagCourseBulkRequest request = new LearningTagCourseBulkRequest(
                Arrays.asList(1L, 2L),
                Arrays.asList("course-sql", "course-elastic", "course-missing", "course-assigned")
        );

        LearningTag tag1 = new LearningTag();
        tag1.setId(1L);
        LearningTag tag2 = new LearningTag();
        tag2.setId(2L);

        when(learningTagRepository.findAllById(request.getLearningTagIds())).thenReturn(Arrays.asList(tag1, tag2));

        CourseEntity sqlCourse = new CourseEntity();
        sqlCourse.setId(10L);
        sqlCourse.setUid("course-sql");
        when(courseRepository.findByUid("course-sql")).thenReturn(Optional.of(sqlCourse));

        CourseEntity assignedCourse = new CourseEntity();
        assignedCourse.setId(20L);
        assignedCourse.setUid("course-assigned");
        when(courseRepository.findByUid("course-assigned")).thenReturn(Optional.of(assignedCourse));

        when(courseRepository.findByUid("course-elastic")).thenReturn(Optional.empty());
        Course elasticCourse = new Course();
        elasticCourse.setId("course-elastic");
        elasticCourse.setTitle("Elastic Course");
        elasticCourse.setShortDescription("Elastic Desc");
        elasticCourse.setStatus(Status.PUBLISHED);
        when(elasticCourseRepository.findById("course-elastic")).thenReturn(Optional.of(elasticCourse));

        CourseStatusEntity statusEntity = new CourseStatusEntity("Published");
        when(courseStatusRepository.findByName("Published")).thenReturn(Optional.of(statusEntity));

        CourseEntity savedElasticCourse = new CourseEntity(elasticCourse.getId(), elasticCourse.getTitle(), elasticCourse.getShortDescription(), statusEntity);
        savedElasticCourse.setId(30L);
        when(courseRepository.save(any(CourseEntity.class))).thenReturn(savedElasticCourse);

        when(courseRepository.findByUid("course-missing")).thenReturn(Optional.empty());
        when(elasticCourseRepository.findById("course-missing")).thenReturn(Optional.empty());

        // For tag 1:
        when(courseTagRepository.findByLearningTagIdAndCourseId(1L, 10L)).thenReturn(Optional.empty());
        when(courseTagRepository.findByLearningTagIdAndCourseId(1L, 20L)).thenReturn(Optional.of(new CourseLearningTagEntity(tag1, assignedCourse)));
        when(courseTagRepository.findByLearningTagIdAndCourseId(1L, 30L)).thenReturn(Optional.empty());

        // For tag 2:
        when(courseTagRepository.findByLearningTagIdAndCourseId(2L, 10L)).thenReturn(Optional.empty());
        when(courseTagRepository.findByLearningTagIdAndCourseId(2L, 20L)).thenReturn(Optional.empty());
        when(courseTagRepository.findByLearningTagIdAndCourseId(2L, 30L)).thenReturn(Optional.empty());

        BulkUpdateResponse<LearningTagCourseUpdateResponse> response = learningTagService.assignCoursesToTag(request);

        assertNotNull(response);
        assertEquals(2, response.getSuccessfulIds().size());
        verify(courseTagRepository, times(5)).save(any(CourseLearningTagEntity.class));
    }

    @Test
    public void testGetLearningTagHyperlinkSuccess() {
        LearningTag tag = new LearningTag();
        tag.setId(1L);

        LearningTagHyperlink hyperlink = new LearningTagHyperlink(100L, tag, "https://test.com", "Title", "Desc", null, null);
        LearningTagHyperlinkDto dto = new LearningTagHyperlinkDto(100L, "Title", "Desc", "https://test.com");

        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(100L, 1L)).thenReturn(Optional.of(hyperlink));
        when(learningTagFactory.createHyperlinkDto(hyperlink)).thenReturn(dto);

        LearningTagHyperlinkDto result = learningTagService.getLearningTagHyperlink(1L, 100L);

        assertEquals(Long.valueOf(100L), result.getId());
        assertEquals("Title", result.getTitle());
        assertEquals("https://test.com", result.getUrl());

        verify(learningTagHyperlinkRepository).findByIdAndLearningTagId(100L, 1L);
        verify(learningTagFactory).createHyperlinkDto(hyperlink);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetLearningTagHyperlinkNotFound() {
        when(learningTagHyperlinkRepository.findByIdAndLearningTagId(999L, 1L)).thenReturn(Optional.empty());
        learningTagService.getLearningTagHyperlink(1L, 999L);
    }
}
