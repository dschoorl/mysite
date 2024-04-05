package info.rsdev.mysite.common.domain;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteTranslations {

    private static final Logger logger = LoggerFactory.getLogger(SiteTranslations.class);

    private final Map<Locale, Map<String, String>> translationsByLocale = new HashMap<>();

    public SiteTranslations(File siteRoot) {
        File[] translationFiles = siteRoot.listFiles((dir, name) -> name.toLowerCase().endsWith(".translations"));
        for (File translationFile : translationFiles) {
            String fileName = translationFile.getName();
            Locale locale = Locale.of(fileName.substring(0, fileName.lastIndexOf('.')));
            try (InputStreamReader reader = new FileReader(translationFile)) {
                Properties properties = new Properties();
                properties.load(reader);
                Map<String, String> asMap = new HashMap<>(properties.size());
                properties.forEach((k, v) -> asMap.put((String) k, (String) v));
                translationsByLocale.put(locale, asMap);
                if (logger.isDebugEnabled()) {
                    logger.debug("Loaded {} translations for {} from {}", asMap.size(), locale, translationFile.getAbsolutePath());
                }
            } catch (IOException e) {
                logger.error("Ignoring translations for {} after IOException: {}", translationFile.getAbsolutePath(),
                        e.getMessage());
            }
        }
    }

    public Map<String, String> getTranslations(Locale language) {
        if (!translationsByLocale.containsKey(language)) {
            return Collections.emptyMap();
        }
        return translationsByLocale.get(language);
    }
}
