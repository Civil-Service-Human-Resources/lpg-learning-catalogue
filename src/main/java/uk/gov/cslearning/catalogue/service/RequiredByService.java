package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.module.Audience;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;

@Service
public class RequiredByService {

    public boolean isAudienceRequiredWithinDays(Audience audience, Instant now, long days) {
        if (audience.getRequiredBy() == null) {
            return false;
        }
        Instant requiredBy = audience.getRequiredBy();
        LocalDate requiredByLocalDate = requiredBy.atOffset(ZoneOffset.UTC).toLocalDate();

        LocalDate nowLocalDate = now.atOffset(ZoneOffset.UTC).toLocalDate();
        LocalDate nowPlusDays = now.atOffset(ZoneOffset.UTC).toLocalDate().plusDays(days);

        if (audience.getFrequency() == null) {
            if (requiredByLocalDate.isBefore(nowLocalDate)) {
                return false;
            } else {
                return isWithinRange(requiredByLocalDate, nowPlusDays);
            }
        } else {
            Period period = Period.parse(audience.getFrequency());
            while (requiredByLocalDate.isBefore(nowLocalDate)) {
                requiredByLocalDate = increment(requiredByLocalDate, period);
            }
            return isWithinRange(requiredByLocalDate, nowPlusDays);
        }
    }

    private LocalDate increment(LocalDate localDate, Period period) {
        return localDate.plus(period);
    }

    private boolean isWithinRange(LocalDate requiredByLocalDate, LocalDate nowPlusDays) {
        return requiredByLocalDate.isBefore(nowPlusDays) || requiredByLocalDate.isEqual(nowPlusDays);
    }
}
