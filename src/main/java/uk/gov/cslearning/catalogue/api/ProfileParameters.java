package uk.gov.cslearning.catalogue.api;

import java.util.ArrayList;

public class ProfileParameters {

    private ArrayList<String> profileDepartments;

    private ArrayList<String> profileGrades;

    private ArrayList<String> profileAreasOfWork = new ArrayList<>();

    private ArrayList<String> profileInterests = new ArrayList<>();

    public ProfileParameters() {}

    public ArrayList<String> getProfileDepartments() {
        return profileDepartments;
    }

    public void setProfileDepartments(ArrayList<String> profileDepartments) {
        this.profileDepartments = profileDepartments;
    }

    public ArrayList<String> getProfileGrades() {
        return profileGrades;
    }

    public void setProfileGrades(ArrayList<String> profileGrades) {
        this.profileGrades = profileGrades;
    }

    public ArrayList<String> getProfileAreasOfWork() {
        return profileAreasOfWork;
    }

    public void setProfileAreasOfWork(ArrayList<String> profileAreasOfWork) {
        this.profileAreasOfWork = profileAreasOfWork;
    }

    public ArrayList<String> getProfileInterests() {
        return profileInterests;
    }

    public void setProfileInterests(ArrayList<String> interests) {
        this.profileInterests = interests;
    }
}


