package uk.gov.cslearning.catalogue.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class CatalogueSearch {

    @RequestMapping
    public String heyThere() {
        return "Hey there";
    }
}
