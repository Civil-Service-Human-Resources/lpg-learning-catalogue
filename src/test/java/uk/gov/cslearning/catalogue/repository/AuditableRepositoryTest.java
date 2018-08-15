package uk.gov.cslearning.catalogue.repository;

import org.assertj.core.util.Lists;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.security.core.Authentication;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.service.AuthenticationFacade;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuditableRepositoryTest {

    @Mock
    private CourseRepository wrappedRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private TestRepository auditableRepository;

    @Test
    public void saveShouldSetCreateAuditFieldsIfEntityDoesNotExist() {
        String username = "test-user";
        Course course = new Course();
        Authentication authentication = mock(Authentication.class);

        when(wrappedRepository.findById(course.getId())).thenReturn(Optional.empty());
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        when(wrappedRepository.save(course)).thenReturn(course);

        Course result = auditableRepository.save(course);

        assertEquals(username, result.getCreatedBy());
        assertTrue(result.getCreatedDate() <= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertTrue(result.getCreatedDate() >= LocalDateTime.now().atZone(ZoneId.systemDefault()).minusSeconds(5).toEpochSecond());
    }

    @Test
    public void saveShouldSetUpdateAuditFieldsIfEntityExists() {
        String createUser = "create-user";
        long createdDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        String username = "test-user";

        Course existingCourse = new Course();
        existingCourse.setCreatedBy(createUser);
        existingCourse.setCreatedDate(createdDate);

        Course updatedCourse = new Course();

        Authentication authentication = mock(Authentication.class);

        when(wrappedRepository.findById(updatedCourse.getId())).thenReturn(Optional.of(existingCourse));

        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        when(wrappedRepository.save(updatedCourse)).thenReturn(updatedCourse);

        Course result = auditableRepository.save(updatedCourse);

        assertEquals(createdDate, result.getCreatedDate());
        assertEquals(createUser, result.getCreatedBy());

        assertEquals(username, result.getModifiedBy());
        assertTrue(result.getModifiedDate() >= LocalDateTime.now().atZone(ZoneId.systemDefault()).minusSeconds(5).toEpochSecond());
        assertTrue(result.getModifiedDate() <= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    public void saveAllWithIterableShouldCallSave() {
        String username = "test-user";
        Course course = new Course();
        Authentication authentication = mock(Authentication.class);

        when(wrappedRepository.findById(course.getId())).thenReturn(Optional.empty());
        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);

        when(wrappedRepository.save(course)).thenReturn(course);

        Course result = Lists.newArrayList(auditableRepository.saveAll(Collections.singletonList(course))).get(0);

        assertEquals(username, result.getCreatedBy());
        assertTrue(result.getCreatedDate() <= LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertTrue(result.getCreatedDate() >= LocalDateTime.now().atZone(ZoneId.systemDefault()).minusSeconds(5).toEpochSecond());
    }

    @Test
    public void indexShouldCallMethodOnWrappedRepository() {
        Course course = new Course();

        auditableRepository.index(course);
        verify(wrappedRepository).index(course);
    }

    @Test
    public void searchWithQueryBuilderShouldCallMethodOnWrappedRepository() {
        QueryBuilder queryBuilder = mock(QueryBuilder.class);

        auditableRepository.search(queryBuilder);
        verify(wrappedRepository).search(queryBuilder);
    }

    @Test
    public void searchWithQueryBuilderAndPageableShouldCallMethodOnWrappedRepository() {

        QueryBuilder queryBuilder = mock(QueryBuilder.class);
        Pageable pageable = mock(Pageable.class);

        auditableRepository.search(queryBuilder, pageable);
        verify(wrappedRepository).search(queryBuilder, pageable);
    }

    @Test
    public void searchWithSearchQueryShouldCallMethodOnWrappedRepository() {
        SearchQuery searchQuery = mock(SearchQuery.class);

        auditableRepository.search(searchQuery);
        verify(wrappedRepository).search(searchQuery);
    }

    @Test
    public void searchSimilarShouldCallMethodOnWrappedRepository() {
        Course course = mock(Course.class);
        String[] fields = {"a"};
        Pageable pageable = mock(Pageable.class);

        auditableRepository.searchSimilar(course, fields, pageable);

        verify(wrappedRepository).searchSimilar(eq(course), eq(fields), eq(pageable));
    }

    @Test
    public void refreshShouldCallMethodOnWrappedRepository() {
        auditableRepository.refresh();
        verify(wrappedRepository).refresh();
    }

    @Test
    public void getEntityClassShouldCallMethodOnWrappedRepository() {
        auditableRepository.getEntityClass();
        verify(wrappedRepository).getEntityClass();
    }

    @Test
    public void findAllWithSortShouldCallWrappedRepository() {
        Sort sort = mock(Sort.class);

        auditableRepository.findAll(sort);
        verify(wrappedRepository).findAll(sort);
    }

    @Test
    public void findAllWithPageableShouldCallWrappedRepository() {
        Pageable pageable = mock(Pageable.class);

        auditableRepository.findAll(pageable);
        verify(wrappedRepository).findAll(pageable);
    }

    @Test
    public void findByIdShouldCallWrappedRepository() {
        String id = "test-id";

        auditableRepository.findById(id);
        verify(wrappedRepository).findById(id);
    }

    @Test
    public void existsByIdShouldCallWrappedRepository() {
        String id = "test-id";

        auditableRepository.existsById(id);
        verify(wrappedRepository).existsById(id);
    }

    @Test
    public void findAllShouldCallWrappedRepository() {
        auditableRepository.findAll();
        verify(wrappedRepository).findAll();
    }

    @Test
    public void findAllByIdShouldCallWrappedRepository() {
        List<String> iterable = Collections.singletonList("id");

        auditableRepository.findAllById(iterable);
        verify(wrappedRepository).findAllById(iterable);
    }

    @Test
    public void countShouldCallWrappedRespository() {
        auditableRepository.count();
        verify(wrappedRepository).count();
    }

    @Test
    public void deleteByIdShouldCallRepository() {
        String id = "test-id";
        auditableRepository.deleteById(id);
        verify(wrappedRepository).deleteById(id);
    }

    @Test
    public void deleteShouldCallWrappedRepository() {
        Course course = new Course();

        auditableRepository.delete(course);
        verify(wrappedRepository).delete(course);
    }

    @Test
    public void deleteAllWithIterableShouldCallWrappedRepository() {
        List<Course> iterable = Collections.singletonList(new Course());

        auditableRepository.deleteAll(iterable);
        verify(wrappedRepository).deleteAll(iterable);
    }

    @Test
    public void deleteAllShouldCallWrappedRepository() {
        auditableRepository.deleteAll();
        verify(wrappedRepository).deleteAll();
    }

    private static class TestRepository extends AuditableRepository<Course, CourseRepository> {
        public TestRepository(CourseRepository wrappedRepository, AuthenticationFacade authenticationFacade) {
            super(wrappedRepository, authenticationFacade);
        }
    }

}