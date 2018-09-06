package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import org.apache.tika.parser.ParseContext;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ParseContextFactoryTest {
    private final ParseContextFactory parseContextFactory = new ParseContextFactory();

    @Test
    public void shouldReturnParseContext() {
        ParseContext parseContext = parseContextFactory.create();
        assertNotNull(parseContext);
    }
}