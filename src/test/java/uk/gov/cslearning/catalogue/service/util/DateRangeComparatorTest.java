package uk.gov.cslearning.catalogue.service.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import uk.gov.cslearning.catalogue.domain.module.DateRange;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DateRangeComparatorTest {

  @Test
  public void shouldSortDate() {
    //Given
    LocalDate date = LocalDate.of(2020,04,07);
    LocalTime start = LocalTime.NOON;
    LocalTime end = LocalTime.MIDNIGHT;

    DateRange dateRange1 = new DateRange();
    dateRange1.setDate(date.plusDays(3));
    dateRange1.setStartTime(start);
    dateRange1.setEndTime(end);

    DateRange dateRange2 = new DateRange();
    dateRange2.setDate(date);
    dateRange2.setStartTime(start);
    dateRange2.setEndTime(end);

    List<DateRange> listOfDates = new ArrayList<>();
    listOfDates.add(dateRange2);
    listOfDates.add(dateRange1);

    String expectedList = "2020-04-07,2020-04-10";

    //When
    Collections.sort(listOfDates, new DateRangeComparator());

    String sortedList = listOfDates
        .stream()
        .map(singleDate -> singleDate.getDate().toString())
        .collect(Collectors.joining(","));

    //Then
    assertTrue("Sorting failed",expectedList.equals(sortedList));
  }
}
