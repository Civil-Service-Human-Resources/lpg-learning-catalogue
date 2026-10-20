package uk.gov.cslearning.catalogue.mapping;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
public class DaysMapperTest {
    private static final String PROPER_VALUES = "1,7,30";
    private static final String ONE_VALUE = "1";
    private static final String TEXT_VALUES = "One,Seven,Thirty";
    private static final String PARTIAL_TEXT_VALUES = "1,Seven,30";

    @Test
    public void shouldMapStringValuesToLongValues() {
        List<Long> values = DaysMapper.convertDaysFromTextToNumeric(PROPER_VALUES);
        assertEquals(3, values.size());
        assertEquals(Long.valueOf(1), values.get(0));
        assertEquals(Long.valueOf(7), values.get(1));
        assertEquals(Long.valueOf(30), values.get(2));
    }

    @Test
    public void shouldMapOneStringValueToLongValue() {
        List<Long> values = DaysMapper.convertDaysFromTextToNumeric(ONE_VALUE);
        assertEquals(1, values.size());
        assertEquals(Long.valueOf(1), values.getFirst());
    }

    @Test
    public void shouldMapEmptyStringToEmptyList() {
        List<Long> values = DaysMapper.convertDaysFromTextToNumeric(StringUtils.EMPTY);
        assertEquals(0, values.size());
    }

    @Test
    public void shouldNotMapTextValues() {
        assertThrows(IllegalArgumentException.class, () -> DaysMapper.convertDaysFromTextToNumeric(TEXT_VALUES));
    }

    @Test
    public void shouldNotMapPartialTextValues() {
        assertThrows(IllegalArgumentException.class, () -> DaysMapper.convertDaysFromTextToNumeric(PARTIAL_TEXT_VALUES));
    }
}
