package uk.gov.cslearning.catalogue.domain.CivilServant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Interest {
    private String name;
}