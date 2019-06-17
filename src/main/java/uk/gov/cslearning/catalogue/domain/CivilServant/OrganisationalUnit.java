package uk.gov.cslearning.catalogue.domain.CivilServant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OrganisationalUnit {
    private String code;
    private String name;
    private List<String> paymentMethods = new ArrayList<>();
    private List<OrganisationalUnit> children = new ArrayList<>();
}