package uk.gov.cslearning.catalogue.domain.module;

public class OnlineModule extends Module {

    private static final String TYPE = "elearning";

    public OnlineModule() {
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
