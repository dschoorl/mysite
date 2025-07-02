package info.rsdev.mysite.text;

import info.rsdev.mysite.common.DefaultConfigKeys;

/**
 * This class defines the property keys that are understood by the document
 * module.
 */
public interface ConfigKeys extends DefaultConfigKeys {

    /**
     * The property name that holds the name of the menu item to display the recent
     * documents under. This allows the site to display recent documents from all
     * the different categories in a single 'meta'-category, based on document age.
     */
    public static final String RECENT_DOCUMENTS_GROUP_NAME_KEY = "recentDocumentsGroupName";

    /**
     * The property name that holds an integer value of the maximum age in days of a
     * document to still be considered recent. Defaults to 90 days.
     */
    public static final String RECENT_DOCUMENTS_DAYS_KEY = "recentDocumentDays";

    /**
     * This property name holds the name of a static resource that is located in the
     * module's root folder and will be served when the path in the module's folder
     * is empty.
     */
    public static final String LANDING_PAGE = "landingPage";
}
