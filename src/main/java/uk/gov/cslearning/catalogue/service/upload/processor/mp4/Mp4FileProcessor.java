package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import com.google.common.collect.ImmutableMap;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.ProcessedFileFactory;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

@Component
public class Mp4FileProcessor implements FileProcessor {

    private final ProcessedFileFactory processedFileFactory;
    private final ParserFactory parserFactory;
    private final ContentHandlerFactory contentHandlerFactory;
    private final MetadataFactory metadataFactory;
    private final ParseContextFactory parseContextFactory;

    public Mp4FileProcessor(ProcessedFileFactory processedFileFactory, ParserFactory parserFactory, ContentHandlerFactory contentHandlerFactory, MetadataFactory metadataFactory, ParseContextFactory parseContextFactory) {
        this.processedFileFactory = processedFileFactory;
        this.parserFactory = parserFactory;
        this.contentHandlerFactory = contentHandlerFactory;
        this.metadataFactory = metadataFactory;
        this.parseContextFactory = parseContextFactory;
    }

    @Override
    public ProcessedFile process(FileUpload fileUpload) {
        Parser parser = parserFactory.createMp4Parser();
        ContentHandler handler = contentHandlerFactory.createBodyContentHandler();
        Metadata metadata = metadataFactory.create();
        ParseContext parseContext = parseContextFactory.create();

        try (InputStream inputStream = fileUpload.getFile().getInputStream()) {
            parser.parse(inputStream, handler, metadata, parseContext);

            Double durationMillis = Double.valueOf(metadata.get(Mp4MetadataKeys.DURATION.toString())) * 1000;
            Duration duration = Duration.ofMillis(durationMillis.longValue());

            long width = Long.parseLong(metadata.get(Mp4MetadataKeys.IMAGE_WIDTH.toString()));
            long length = Long.parseLong(metadata.get(Mp4MetadataKeys.IMAGE_HEIGHT.toString()));

            Map<String, Object> metadataMap = ImmutableMap.of("duration", duration, "imageWidth", width, "imageHeight", length);

            return processedFileFactory.create(fileUpload, metadataMap);
        } catch (IOException | SAXException | TikaException e) {
            throw new FileUploadException(e);
        }
    }
}
