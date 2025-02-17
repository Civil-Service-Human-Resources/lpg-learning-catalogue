package uk.gov.cslearning.catalogue.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class ProfileParameters {

    private ArrayList<String> profileDepartments = new ArrayList<>();
    private ArrayList<String> profileGrades = new ArrayList<>();
    private ArrayList<String> profileAreasOfWork = new ArrayList<>();
    private ArrayList<String> profileInterests = new ArrayList<>();
}

