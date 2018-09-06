package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp4.MP4Parser;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ParserFactoryTest {
    private final ParserFactory parserFactory = new ParserFactory();

    @Test
    public void shouldReturnMp4Parser() {
        Parser parser = parserFactory.createMp4Parser();
        assertTrue(parser instanceof MP4Parser);
    }
}