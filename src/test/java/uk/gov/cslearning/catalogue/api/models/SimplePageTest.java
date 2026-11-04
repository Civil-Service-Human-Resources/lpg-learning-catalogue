package uk.gov.cslearning.catalogue.api.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimplePageTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldConstructWithContentTotalAndPageable() {
        List<String> content = List.of("item1", "item2");
        Pageable pageable = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "name"));
        SimplePage<String> page = new SimplePage<>(content, 10, pageable);

        assertEquals(content, page.getContent());
        assertEquals(10, page.getTotalElements());
        assertEquals(10, page.getTotalResults());
        assertEquals(5, page.getTotalPages());
        assertEquals(1, page.getNumber());
        assertEquals(1, page.getPage());
        assertEquals(2, page.getSize());
        assertEquals(2, page.getNumberOfElements());
        assertTrue(page.hasContent());
        assertFalse(page.isFirst());
        assertFalse(page.isLast());
        assertTrue(page.hasNext());
        assertTrue(page.hasPrevious());
        assertEquals(List.of("name,ASC"), page.getSortList());
        assertEquals(pageable, page.getPageable());
        assertNotNull(page.nextPageable());
        assertNotNull(page.previousPageable());
        assertNotNull(page.iterator());
    }

    @Test
    public void shouldConstructWithDelegate() {
        Page<String> delegate = new PageImpl<>(List.of("item1"), PageRequest.of(0, 1), 1);
        SimplePage<String> page = new SimplePage<>(delegate);

        assertEquals(1, page.getContent().size());
        assertEquals(1, page.getTotalElements());
        assertTrue(page.isFirst());
        assertTrue(page.isLast());
        assertFalse(page.hasNext());
        assertFalse(page.hasPrevious());
    }

    @Test
    public void shouldSupportMapFunction() {
        SimplePage<String> page = new SimplePage<>(List.of("1", "2"), 2, PageRequest.of(0, 2));
        Page<Integer> mapped = page.map(Integer::parseInt);

        assertEquals(List.of(1, 2), mapped.getContent());
        assertEquals(2, mapped.getTotalElements());
    }

    @Test
    public void shouldSerializeAndDeserializeJson() throws Exception {
        SimplePage<String> page = new SimplePage<>(List.of("alpha", "beta"), 2, PageRequest.of(0, 2, Sort.by("title,ASC")));
        String json = objectMapper.writeValueAsString(page);

        assertNotNull(json);
        assertTrue(json.contains("\"content\":[\"alpha\",\"beta\"]"));

        SimplePage<String> deserialized = objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(SimplePage.class, String.class));
        assertEquals(page.getContent(), deserialized.getContent());
        assertEquals(page.getTotalElements(), deserialized.getTotalElements());
        assertEquals(page.getNumber(), deserialized.getNumber());
        assertEquals(page.getSize(), deserialized.getSize());
    }

    @Test
    public void shouldImplementEqualsAndHashCodeAndToString() {
        Pageable pageable = PageRequest.of(0, 5);
        SimplePage<String> page1 = new SimplePage<>(List.of("a", "b"), 2, pageable);
        SimplePage<String> page2 = new SimplePage<>(List.of("a", "b"), 2, pageable);
        SimplePage<String> page3 = new SimplePage<>(List.of("c"), 1, pageable);

        assertEquals(page1, page2);
        assertNotEquals(page1, page3);
        assertEquals(page1.hashCode(), page2.hashCode());
        assertNotNull(page1.toString());
        assertNotEquals(page1, "someString");
    }
}
