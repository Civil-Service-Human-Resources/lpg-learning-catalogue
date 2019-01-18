package uk.gov.cslearning.catalogue.domain.CivilServant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.domain.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CivilServant {
    private String fullName;
    private Grade grade;
    private OrganisationalUnit organisationalUnit;
    private Profession profession;
    private LearningProvider learningProvider;
    private List<Profession> otherAreasOfWork = new ArrayList<>();
    private List<Interest> interests = new ArrayList<>();
    private Scope scope;

    public Optional<OrganisationalUnit> getOrganisationalUnit() {
        return Optional.ofNullable(this.organisationalUnit);
    }

    public Optional<Profession> getProfession() {
        return Optional.ofNullable(this.profession);
    }

    public Optional<LearningProvider> getLearningProvider() {
        return Optional.ofNullable(this.learningProvider);
    }

    public Optional<String> getOrganisationalUnitCode() {
        return getOrganisationalUnit().map(OrganisationalUnit::getCode);
    }

    public Optional<Long> getProfessionId() {
        return getProfession().map(Profession::getId);
    }

    public Optional<String> getProfessionName() {
        return getProfession().map(Profession::getName);
    }

    public Optional<String> getLearningProviderId() {
        return getLearningProvider().map(LearningProvider::getId);
    }
}