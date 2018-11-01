package uk.gov.cslearning.catalogue.domain.module;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import java.time.Instant;

public class DateRange {

    private Instant startDateTime;

    private Instant endDateTime;

    public DateRange() {
    }

    public DateRange(@NotNull Instant startDateTime, @NotNull Instant endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Instant startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Instant getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Instant endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DateRange dateRange = (DateRange) o;

        return new EqualsBuilder()
                .append(startDateTime, dateRange.startDateTime)
                .append(endDateTime, dateRange.endDateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(startDateTime)
                .append(endDateTime)
                .toHashCode();
    }
}
