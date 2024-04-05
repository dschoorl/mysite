package info.rsdev.mysite.common;

import java.util.Map;

/**
 * Manage a {@link ThreadLocal} variable that holds the translations applicable
 * to the requested website and the requested locale. This data is passed on to
 * {@link RequestHandler} via ThreadLocal, because not all handlers need
 * translations. This way we don't burden the API when it is only needed in a
 * few cases.
 */
public class LocalizationContext {

    private static final ThreadLocal<Map<String, String>> CURRENT_LOCALIZATIONS = new ThreadLocal<>();

    public static void setLocalizations(Map<String, String> translations) {
        CURRENT_LOCALIZATIONS.set(translations);
    }

    public static Map<String, String> getLocalizations() {
        return CURRENT_LOCALIZATIONS.get();
    }

    public static void clear() {
        CURRENT_LOCALIZATIONS.set(null);
    }

}
