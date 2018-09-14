package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp4.MP4Parser;
import org.springframework.stereotype.Component;

@Component
public class ParserFactory {
    public Parser createMp4Parser() {
        return new MP4Parser();
    }

    public Parser create() {
        return new AutoDetectParser();
    }
}
