package info.rsdev.mysite.stats;

import info.rsdev.mysite.common.DefaultConfigKeys;
import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.common.domain.DefaultMenuGroup;
import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.MenuItem;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.gallery.GalleryModuleConfig;
import info.rsdev.mysite.stats.domain.AccessLogIterator;
import info.rsdev.mysite.stats.domain.AccessLogReport;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.stringtemplate.v4.ST;

/**
 * This {@link RequestHandler} implementation is responsible for coordinating access to the images from the configured image
 * collection, using the configured template.
 */
public class StatsContentServant implements RequestHandler, DefaultConfigKeys {
    
    /**
     * Cache the queries for UserAgent strings to Bot/crawler agents. The value of the entry is a static string 'Crawler'
     */
    private final ConcurrentHashMap<String, String> crawlerUserAgents = new ConcurrentHashMap<>();

    /**
     * Cache the queries for ip2Country mapping
     */
    private final ConcurrentHashMap<String, Locale> ip2Country = new ConcurrentHashMap<>();
    
    @Override
    public String handle(ModuleConfig config, List<MenuGroup> menu, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (config == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(config instanceof StatsModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s. please check"
                    + "the value of property '%s'", GalleryModuleConfig.class.getSimpleName(), config, MODULECONFIGTYPE_KEY));
        }
        StatsModuleConfig statsConfig = (StatsModuleConfig) config;
        
        AccessLogReport report = new AccessLogReport(config.getString(SITENAME_KEY), this.ip2Country, this.crawlerUserAgents);
        AccessLogIterator logItems = new AccessLogIterator(statsConfig.getAccessLogFile());
        while (logItems.hasNext()) {
            report.process(logItems.next());
        }
        
        BasicPageModel<StatsModuleConfig> model = new BasicPageModel<>(statsConfig, null);
        renderPage(response, model, report);
        
        return null;
    }
    
    private void renderPage(HttpServletResponse response, BasicPageModel<StatsModuleConfig> model, AccessLogReport report) throws ServletException {
        StatsModuleConfig config = model.getConfig();
        ST template = config.getTemplate();
        try {
            if (template == null) {
                response.sendError(404);
            } else {
                template.add("report", report);
                template.add("model", model);
                response.getWriter().write(template.render());
                response.setContentType("text/html");
            }
        } catch (IOException e) {
            throw new ServletException("Error occured during preparation of web page", e);
        }
    }

    @Override
    public MenuGroup getMenuItems(ModuleConfig config) {
        return new DefaultMenuGroup(Collections.<MenuItem>emptyList());
    }
    
}
