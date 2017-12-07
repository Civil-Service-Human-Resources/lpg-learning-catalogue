package uk.gov.civilservice.learningcatalogue;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String heyThere() {
        return "Hey there";
    }
}
