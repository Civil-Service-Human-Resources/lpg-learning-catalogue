package uk.gov.cslearning.catalogue.domain.Owner;

import lombok.Data;

@Data
public class Owner {
    private String scope;
    private String organisationalUnit;
    private Long profession;
    private String learningProvider;
}
