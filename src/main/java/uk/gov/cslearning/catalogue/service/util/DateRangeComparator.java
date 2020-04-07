package uk.gov.cslearning.catalogue.service.util;

import uk.gov.cslearning.catalogue.domain.module.DateRange;
import java.time.LocalDateTime;
import java.util.Comparator;

public class DateRangeComparator implements Comparator<DateRange> {

  @Override
  public int compare(final DateRange dateObj1, final DateRange dateObj2) {
    final LocalDateTime date1WithTime = LocalDateTime.of(
        dateObj1.getDate(),dateObj1.getStartTime());
    final LocalDateTime date2WithTime = LocalDateTime.of(
        dateObj2.getDate(),dateObj1.getStartTime());

    return date1WithTime.compareTo(date2WithTime);
  }

}
