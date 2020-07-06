package info.rsdev.mysite.redirect;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;
import info.rsdev.mysite.exception.ConfigurationException;

/**
 * Based on the browsers language, redirect either to the part of the web site
 * that serves content in that language, or redirect to the web sites default
 * language.
 */
public class LanguageRedirectServant implements RequestHandler, ConfigKeys {

    @Override
    public ModuleHandlerResult handle(ModuleConfig config, List<MenuGroup> menu, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        if (config == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(config instanceof LanguageRedirectModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s. please check"
                    + "the value of property '%s'", LanguageRedirectModuleConfig.class.getSimpleName(), config,
                    MODULECONFIGTYPE_KEY));
        }
        LanguageRedirectModuleConfig redirectConfig = (LanguageRedirectModuleConfig) config;
        Map<Locale, String> localesByPath = redirectConfig.getLocalesToPathMapping();

        String redirectPath = null;
        Enumeration<Locale> browserLocales = request.getLocales();
        while (browserLocales.hasMoreElements()) {
            Locale locale = browserLocales.nextElement();
            if (localesByPath.containsKey(locale)) {
                redirectPath = localesByPath.get(locale);
                break;
            }
        }
        
        if (redirectPath == null) {
            //redirect to default language
            Locale defaultLocale = redirectConfig.getDefaultLocale().orElse(null);
            redirectPath = localesByPath.get(defaultLocale);
        }
        
        if ((redirectPath == null) && !localesByPath.isEmpty()) {
            redirectPath = localesByPath.values().iterator().next();    //arbitrairly select a localization
        }

        if (redirectPath != null) {
            response.sendRedirect(redirectPath);
            return ModuleHandlerResult.HANDLED;
        }
        
        return ModuleHandlerResult.NO_CONTENT;
    }

    @Override
    public MenuGroup getMenuItems(ModuleConfig config) {
        return null; // this module contributes no menu items to the GUI
    }
}
