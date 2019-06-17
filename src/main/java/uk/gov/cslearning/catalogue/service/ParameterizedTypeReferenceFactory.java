package uk.gov.cslearning.catalogue.service;

import com.google.common.collect.ImmutableMap;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.CivilServant.OrganisationalUnit;
import uk.gov.cslearning.catalogue.exception.IllegalTypeException;

import java.util.List;
import java.util.Map;

@Component
public class ParameterizedTypeReferenceFactory {
    /**
     * The maps below are a nasty hack. Unfortunately it's necessary because of the way ParameterizedTypeReference is implemented.
     * See https://stackoverflow.com/questions/21987295/using-spring-resttemplate-in-generic-method-with-generic-parameter
     */

    private final Map<String, ParameterizedTypeReference> listParameterizedTypeReferenceMap = ImmutableMap.of(
            "uk.gov.cslearning.catalogue.domain.CivilServant.OrganisationalUnit", new ParameterizedTypeReference<List<OrganisationalUnit>>() {
            }
    );

    <T> ParameterizedTypeReference<List<T>> createListReference(Class<T> type) throws IllegalTypeException {
        if (listParameterizedTypeReferenceMap.containsKey(type.getName())) {
            return listParameterizedTypeReferenceMap.get(type.getName());
        }

        throw new IllegalTypeException(type);
    }
}
