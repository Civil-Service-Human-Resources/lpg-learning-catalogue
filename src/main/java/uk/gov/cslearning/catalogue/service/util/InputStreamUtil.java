package uk.gov.cslearning.catalogue.service.util;

import java.io.*;

public class InputStreamUtil {
    public static File saveInputStreamAsTempFile(InputStream inputStream) throws IOException {
        File tempInputStreamFile = File.createTempFile("tempinputstream", ".tmp");
        tempInputStreamFile.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(tempInputStreamFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        return tempInputStreamFile;
    }

    public static InputStream getInputStreamFromFile(File file) throws IOException {
        return new FileInputStream(file);
    }
}
