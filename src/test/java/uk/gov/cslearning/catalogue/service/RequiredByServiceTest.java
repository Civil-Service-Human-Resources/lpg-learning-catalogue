package uk.gov.cslearning.catalogue.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.module.Audience;

import java.time.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RequiredByServiceTest {

    @InjectMocks
    private RequiredByService requiredByService;

    @Test
    public void shouldReturnFalseIfNoRequiredBy() {
        LocalDate dateTime = LocalDate.of(2017, Month.JUNE, 20);
        Instant now = dateTime.atStartOfDay((ZoneId.systemDefault())).toInstant();

        Audience audience = new Audience();

        assertFalse(requiredByService.isAudienceRequiredWithinDays(audience, now, 7));
    }

    @Test
    public void shouldReturnTrueIfRequiredBeforeDaysRequiredBy() {
        LocalDate nowDateTime = LocalDate.of(2017, Month.JUNE, 20);
        Instant now = nowDateTime.atStartOfDay((ZoneId.systemDefault())).toInstant();

        LocalDate requiredByDateTime = LocalDate.of(2017, Month.JUNE, 24);
        Instant requiredBy = requiredByDateTime.atStartOfDay().toInstant(ZoneOffset.UTC);

        Audience audience = new Audience();
        audience.setRequiredBy(requiredBy);

        assertTrue(requiredByService.isAudienceRequiredWithinDays(audience, now, 7));
    }

    @Test
    public void shouldReturnFalseIfRequiredAfterDaysRequiredBy() {
        LocalDate nowDateTime = LocalDate.of(2017, Month.JUNE, 20);
        Instant now = nowDateTime.atStartOfDay((ZoneId.systemDefault())).toInstant();

        LocalDate requiredByDateTime = LocalDate.of(2017, Month.JUNE, 29);
        Instant requiredBy = requiredByDateTime.atStartOfDay().toInstant(ZoneOffset.UTC);

        Audience audience = new Audience();
        audience.setRequiredBy(requiredBy);

        assertFalse(requiredByService.isAudienceRequiredWithinDays(audience, now, 7));
    }

    @Test
    public void shouldReturnFalseIfRequiredBeforeToday() {
        LocalDate nowDateTime = LocalDate.of(2017, Month.JUNE, 20);
        Instant now = nowDateTime.atStartOfDay((ZoneId.systemDefault())).toInstant();

        LocalDate requiredByDateTime = LocalDate.of(2017, Month.JUNE, 10);
        Instant requiredBy = requiredByDateTime.atStartOfDay().toInstant(ZoneOffset.UTC);

        Audience audience = new Audience();
        audience.setRequiredBy(requiredBy);

        assertFalse(requiredByService.isAudienceRequiredWithinDays(audience, now, 7));
    }

    @Test
    public void shouldReturnTrueIfReqByPlusFrequencyIsRequired() {
        LocalDate nowDateTime = LocalDate.of(2017, Month.JUNE, 20);
        Instant now = nowDateTime.atStartOfDay((ZoneId.systemDefault())).toInstant();

        LocalDate requiredByDateTime = LocalDate.of(2016, Month.JUNE, 22);
        Instant requiredBy = requiredByDateTime.atStartOfDay().toInstant(ZoneOffset.UTC);

        Audience audience = new Audience();
        audience.setRequiredBy(requiredBy);
        audience.setFrequency("P1Y");

        assertTrue(requiredByService.isAudienceRequiredWithinDays(audience, now, 7));
    }

    @Test
    public void shouldReturnFalseIfReqByPlusFrequencyIsNotRequired() {
        LocalDate nowDateTime = LocalDate.of(2017, Month.JUNE, 20);
        Instant now = nowDateTime.atStartOfDay((ZoneId.systemDefault())).toInstant();

        LocalDate requiredByDateTime = LocalDate.of(2016, Month.JUNE, 29);
        Instant requiredBy = requiredByDateTime.atStartOfDay().toInstant(ZoneOffset.UTC);

        Audience audience = new Audience();
        audience.setRequiredBy(requiredBy);
        audience.setFrequency("P1Y");

        assertFalse(requiredByService.isAudienceRequiredWithinDays(audience, now, 7));
    }
}