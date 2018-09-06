package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import org.apache.tika.parser.ParseContext;
import org.springframework.stereotype.Component;

@Component
public class ParseContextFactory {
    public ParseContext create() {
        return new ParseContext();
    }
}
