package uk.gov.cslearning.catalogue.api;

import java.util.List;

public class FilterParameters {

    private List<String> types;

    private List<String> departments;

    private List<String> interests;

    private List<String> areasOfWork;

    private String cost;

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public void setDepartments(List<String> departments) {
        this.departments = departments;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getAreasOfWork() {
        return areasOfWork;
    }

    public void setAreasOfWork(List<String> areasOfWork) {
        this.areasOfWork = areasOfWork;
    }

    public boolean hasCost() {
        return this.cost != null && this.cost.length() > 0;
    }

    public boolean hasTypes() {
        return this.types != null && !this.types.isEmpty();
    }
}
