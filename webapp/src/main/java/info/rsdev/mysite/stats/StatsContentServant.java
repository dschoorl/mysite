package info.rsdev.mysite.stats;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.stringtemplate.v4.ST;

import info.rsdev.mysite.common.DefaultConfigKeys;
import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.common.domain.DefaultMenuGroup;
import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.MenuItem;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.gallery.GalleryModuleConfig;
import info.rsdev.mysite.stats.domain.AccessLogIterator;
import info.rsdev.mysite.stats.domain.AccessLogReport;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This {@link RequestHandler} implementation is responsible for coordinating access to the images from the configured image
 * collection, using the configured template.
 */
public class StatsContentServant implements RequestHandler, DefaultConfigKeys {
    
    @Override
    public ModuleHandlerResult handle(ModuleConfig config, List<MenuGroup> menu, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (config == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(config instanceof StatsModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s. please check"
                    + "the value of property '%s'", GalleryModuleConfig.class.getSimpleName(), config, MODULECONFIGTYPE_KEY));
        }
        StatsModuleConfig statsConfig = (StatsModuleConfig) config;
        
        AccessLogReport report = new AccessLogReport(config.getString(SITENAME_KEY));
        AccessLogIterator logItems = new AccessLogIterator(statsConfig.getAccessLogFile());
        while (logItems.hasNext()) {
            report.process(logItems.next());
        }
        
        BasicPageModel<StatsModuleConfig> model = new BasicPageModel<>(statsConfig, null);
        String templateName = renderPage(response, model, report);
        
        return new ModuleHandlerResult(templateName, "stats");
    }
    
    private String renderPage(HttpServletResponse response, BasicPageModel<StatsModuleConfig> model, AccessLogReport report) throws ServletException {
        StatsModuleConfig config = model.getConfig();
        ST template = config.getTemplate(null);
        try {
            if (template == null) {
                response.sendError(404);
            } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");
                template.add("report", report);
                template.add("model", model);
//                template.add("locale", config.getLocale().getLanguage());
                response.getWriter().write(template.render());
            }
        } catch (IOException e) {
            throw new ServletException("Error occured during preparation of web page", e);
        }
        return template==null?null:template.getName();
    }

    @Override
    public MenuGroup getMenuItems(ModuleConfig config) {
        return new DefaultMenuGroup(Collections.<MenuItem>emptyList());
    }
    
}
