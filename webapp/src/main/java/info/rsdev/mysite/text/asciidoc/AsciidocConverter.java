package info.rsdev.mysite.text.asciidoc;

import static org.asciidoctor.Asciidoctor.Factory.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

import org.asciidoctor.Asciidoctor;

public class AsciidocConverter {

    public static final AsciidocConverter INSTANCE = new AsciidocConverter();

    private final Asciidoctor asciidoctor;

    private AsciidocConverter() {
        asciidoctor = create();
    }

    public String convertDocument(File document) {
        try {
            String rawText = new String(Files.readAllBytes(document.toPath()), Charset.defaultCharset());
            return asciidoctor.convert(rawText, getDefaultOptions());
        } catch (IOException e) {
            throw new RuntimeException("Exception occured when reading file: " + document, e);
        }
    }

    private Map<String, Object> getDefaultOptions() {
        return Collections.emptyMap();
    }

}
