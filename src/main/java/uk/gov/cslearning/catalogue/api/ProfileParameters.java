package uk.gov.cslearning.catalogue.api;

import java.util.ArrayList;

public class ProfileParameters {

    private String profileDepartment;

    private String profileGrade;

    private ArrayList<String> profileAreasOfWork = new ArrayList<>();

    private ArrayList<String> profileInterests = new ArrayList<>();

    public ProfileParameters() {}

    public String getProfileDepartment() {
        return profileDepartment;
    }

    public void setProfileDepartment(String profileDepartment) {
        this.profileDepartment = profileDepartment;
    }

    public String getProfileGrade() {
        return profileGrade;
    }

    public void setProfileGrade(String profileGrade) {
        this.profileGrade = profileGrade;
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

    public boolean hasInterests() {
        return profileInterests != null && profileInterests.size() != 0;
    }

    public boolean hasAreasOfWork() {
        return profileAreasOfWork != null && profileAreasOfWork.size() != 0;
    }
}


