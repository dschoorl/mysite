package info.rsdev.mysite.redirect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import info.rsdev.mysite.common.AbstractModuleConfig;
import info.rsdev.mysite.common.RequestHandler;

public class LanguageRedirectModuleConfig extends AbstractModuleConfig implements ConfigKeys {

    /**
     * Map each supported locale to the url path element to access the sites
     * localized content
     */
    private final Map<Locale, String> localeToPathMapping;

    /**
     * Implementation of a {@link RequestHandler} to redirect requests based on
     * browser language
     */
    private final LanguageRedirectServant requestHandler;

    public LanguageRedirectModuleConfig(Properties configProperties) {
        super(configProperties);
        requestHandler = new LanguageRedirectServant();
        this.localeToPathMapping = readLocaleToPathMapping();
    }

    private Map<Locale, String> readLocaleToPathMapping() {
        Map<Locale, String> localesByPath = new HashMap<>();
        for (Entry<Object, Object> entry: getProperties().entrySet()) {
            if (((String)entry.getKey()).startsWith(LOCALE_PREFIX_KEY + ".")) {
                String localeName = ((String)entry.getKey()).substring(LOCALE_PREFIX_KEY.length()+1);
                if (StringUtils.isNotBlank(localeName)) {
                    localesByPath.put(Locale.forLanguageTag(localeName), (String)entry.getValue());
                }
            }
        }

        return Collections.unmodifiableMap(localesByPath);
    }

    @Override
    public RequestHandler getRequestHandler() {
        return requestHandler; // implementation is stateless
    }

    @Override
    public boolean hasHandlerFor(String requestPath) {
        return requestPath.equals(properties.getProperty(MOUNTPOINT_KEY));
    }

    public Map<Locale, String> getLocalesToPathMapping() {
        return localeToPathMapping;
    }

    public Optional<Locale> getDefaultLocale() {
        String defaultLocaleName = getString(DEFAULT_LOCALE_KEY_NAME);
        return defaultLocaleName == null ? Optional.empty() : Optional.of(Locale.forLanguageTag(defaultLocaleName));
    }
}
