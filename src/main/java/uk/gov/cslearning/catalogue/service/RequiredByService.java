package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.module.Audience;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;

@Service
public class RequiredByService {

    public boolean isAudienceRequiredWithinRange(Audience audience, Instant now, long from, long to) {
        if (audience.getRequiredBy() == null) {
            return false;
        }
        Instant requiredBy = audience.getRequiredBy();
        LocalDate requiredByLocalDate = requiredBy.atOffset(ZoneOffset.UTC).toLocalDate();

        LocalDate nowLocalDate = now.atOffset(ZoneOffset.UTC).toLocalDate();

        LocalDate nowPlusFrom = now.atOffset(ZoneOffset.UTC).toLocalDate().plusDays(from);
        LocalDate nowPlusTo = now.atOffset(ZoneOffset.UTC).toLocalDate().plusDays(to);

        if (audience.getFrequency() == null) {
            if (requiredByLocalDate.isBefore(nowLocalDate)) {
                return false;
            } else {
                return isWithinRange(requiredByLocalDate, nowPlusFrom, nowPlusTo);
            }
        } else {
            Period period = Period.parse(audience.getFrequency());
            while (requiredByLocalDate.isBefore(nowLocalDate)) {
                requiredByLocalDate = increment(requiredByLocalDate, period);
            }
            return isWithinRange(requiredByLocalDate, nowPlusFrom, nowPlusTo);
        }
    }

    private LocalDate increment(LocalDate localDate, Period period) {
        return localDate.plus(period);
    }

    private boolean isWithinRange(LocalDate requiredByLocalDate, LocalDate nowPlusFrom, LocalDate nowPlusTo) {
        return requiredByLocalDate.isAfter(nowPlusFrom) && (requiredByLocalDate.isBefore(nowPlusTo) || requiredByLocalDate.isEqual(nowPlusTo));
    }
}
