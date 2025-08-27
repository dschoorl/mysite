package info.rsdev.mysite.stats;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.stringtemplate.v4.ST;

import info.rsdev.mysite.common.DefaultConfigKeys;
import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.common.domain.BasicPageModel;
import info.rsdev.mysite.common.domain.DefaultMenuGroup;
import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.MenuItem;
import info.rsdev.mysite.common.domain.accesslog.AccessLogEntry;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.gallery.GalleryModuleConfig;
import info.rsdev.mysite.stats.domain.AccessLogIterator;
import info.rsdev.mysite.stats.domain.AccessLogReport;
import info.rsdev.mysite.util.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This {@link RequestHandler} implementation is responsible for coordinating access to the images from the configured image
 * collection, using the configured template.
 */
public class StatsContentServant implements RequestHandler, DefaultConfigKeys {
    
    /**
     * Pattern to validate that a period has the format yyyy-mm, e.g. 2025-08.
     * Minimum number of year is 2020, before that time mysite statistics did not
     * exist
     */
    private static final Pattern periodExpression = Pattern.compile("^(20[2-9]\\d)-((?:0[1-9])|(?:1[0-2]))$");

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
        
        //create access report for the year / month of interest
        String path = ServletUtils.getPathPartAfterMountpoint(request.getPathInfo(), statsConfig.getMountPoint());
        String period = extractPeriod(path);
        int targetYear = getTargetYear(period);
        int targetMonth = getTargetMonth(period);
        AccessLogReport report = new AccessLogReport(config.getString(SITENAME_KEY));
        AccessLogIterator logItems = new AccessLogIterator(statsConfig.getAccessLogFile());
        while (logItems.hasNext()) {
            AccessLogEntry logEntry = logItems.next();
            if (logEntry.getYear() > targetYear || (logEntry.getYear() == targetYear && logEntry.getMonth() > targetMonth)) {
                break;  //quit processing log entries in future months; they are irrelevant
            }
            if (logEntry.getYear() == targetYear && logEntry.getMonth() == targetMonth) {
                report.process(logEntry);
            }
        }

        BasicPageModel<StatsModuleConfig> model = new BasicPageModel<>(statsConfig, null);
        String templateName = renderPage(response, model, report);
        
        return new ModuleHandlerResult(templateName, "stats");
    }
    
    private int getTargetYear(String period) {
        int currentYear = LocalDate.now().getYear();
        if (period != null) {
            // Use the provided period, unless it is in the future
            return Math.min(Integer.parseInt(period.substring(0, 4)), currentYear);
        }
        return currentYear;
    }

    private int getTargetMonth(String period) {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue() - 1;
        if (period != null) {
            int periodYear = Integer.parseInt(period.substring(0, 4));
            int periodMonth = Integer.parseInt(period.substring(5)) - 1;
            if (currentYear == periodYear) {
                // Use the provided month, unless it is a future month in this year
                return Math.min(currentMonth, periodMonth);
            } else if (periodYear < currentYear) {
                return periodMonth;
            }
        }
        
        return currentMonth;
    }
    
    private String extractPeriod(String pathInfo) {
        if (periodExpression.matcher(pathInfo).matches()) {
            return pathInfo;
        }
        return null;
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
