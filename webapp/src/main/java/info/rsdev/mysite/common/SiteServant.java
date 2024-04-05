package info.rsdev.mysite.common;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.accesslog.AccessLogEntryV1;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;
import info.rsdev.mysite.common.startup.PropertiesModule.ContentRoot;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.stats.Ip2CountryService;
import info.rsdev.mysite.util.ServletUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Singleton
public class SiteServant extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(SiteServant.class);
    private static final Logger GLOBAL_ACCESS_LOGGER = LoggerFactory.getLogger("AccessLog");
    private static final String GLOBAL_UNAVAILABLE_PAGE = "<!DOCTYPE html>\n" + " <head>\n" + "  <style>\n" + "    body {\n"
            + "        background: grey;\n" + "        font-family: Arial;\n" + "        font-size: 36px }" + "    section {\n"
            + "        background: grey;\n" + "        color: black;\n" + "        border-radius: 1em;\n"
            + "        padding: 1em;\n" + "        position: absolute;\n" + "        top: 50%;\n" + "        left: 50%;\n"
            + "        margin-right: -50%;\n" + "        transform: translate(-50%, -50%) }\n" + "  </style></head>\n"
            + "  <section>\n" + "\n" + "  <p>TEMPORARILY<br />&nbsp;UNAVAILABLE</p>\n" + "\n" + "  </section>\n";

    private static final String ROBOTS_TXT = "robots.txt";
    private static final String FAVICON = "favicon.ico";
    private static final String FAVICON_URL = "/" + FAVICON;
    private static final String ROBOTS_TXT_URL = "/" + ROBOTS_TXT;

    private ConfigDAI configDai = null;

    private Ip2CountryService ip2CountryLookup;

    private final File contentRoot;

    @Inject
    public SiteServant(ConfigDAI configDao, @ContentRoot File contentRoot) {
        this.configDai = configDao;
        this.contentRoot = contentRoot;
        Path ip2CountryCsvFile = contentRoot.toPath().resolve("IP2LOCATION-LITE-DB1.CSV");
        this.ip2CountryLookup = new Ip2CountryService(ip2CountryCsvFile);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // Currently this servlet is not configurable -- this may change in the
        // future
        logger.info(String.format("Initializing Servlet %s [%s]", getServletInfo(), getClass().getName()));
        super.init(config);
        logger.info(String.format("Platform default encoding: %s", Charset.defaultCharset()));
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (isFaviconOrRobotsRequest(request.getPathInfo())) {
            try {
                shortCircuitFaviconOrRobotsRequest(request.getPathInfo(), request, response);
            } catch (IOException e) {
                logger.error("Error loading {}: {}", request.getPathInfo(), e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return;
        }

        ModuleHandlerResult result = null;
        AccessLogEntryV1 logEntry = new AccessLogEntryV1().feedRequest(request, ip2CountryLookup);
        ModuleConfig moduleConfig = null;
        try {
            String modulePath = request.getPathInfo();
            if ((modulePath == null) || modulePath.isEmpty()) {
                modulePath = "/";
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Received request: %s", modulePath));
            }

            String hostname = request.getServerName().toLowerCase();
            SiteConfig config = configDai.getConfig(hostname);

            // get path into module context and then get page for path from the
            // appropriate module
            moduleConfig = config.getModuleConfig(modulePath);
            if (moduleConfig == null) {
                logger.error(String.format("No module configured to serve module path '%s' for %s", modulePath, hostname));
                ((HttpServletResponse) response).sendError(404);
                return;
            }
            if (moduleConfig.isDisabled()) {
                logger.info(String.format("%s is disabled for %s; serving unavailable page", moduleConfig, hostname));
                writeUnavaliablePage((HttpServletResponse) response);
                return;
            }
            logEntry.feedModuleConfig(moduleConfig);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("%s will be serving %s request: %s [QuesryString=%s]", moduleConfig.getRequestHandler(),
                        request.getMethod(), request.getServletPath(), request.getQueryString()));
            }
            List<MenuGroup> menu = config.getMenu(moduleConfig.getLocale());
            result = moduleConfig.getRequestHandler().handle(moduleConfig, menu, (HttpServletRequest) request,
                    (HttpServletResponse) response);

            if (!result.isAlreadyHandled() && result.equals(ModuleHandlerResult.NO_CONTENT)) {
                response.setStatus(404);
            }
        } catch (ConfigurationException e) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(404);
            httpResponse.getWriter().write(e.getLocalizedMessage());
            logger.info(e.getLocalizedMessage());
        } catch (RuntimeException e) {
            ((HttpServletResponse) response).setStatus(500);
            throw e;
        } finally {
            // write entry to access logfile
            Logger accessLogger = null;
            if ((moduleConfig == null) || (moduleConfig.getAccessLogger() == null)) {
                accessLogger = GLOBAL_ACCESS_LOGGER;
            } else {
                accessLogger = moduleConfig.getAccessLogger();
            }
            logEntry.markFinished(result, ((HttpServletResponse) response).getStatus());
            if (!logEntry.ignoreMe()) {
                accessLogger.info(logEntry.toString());
            }
        }
    }

    private boolean isFaviconOrRobotsRequest(String pathInfo) {
        return FAVICON_URL.equals(pathInfo) || ROBOTS_TXT_URL.equals(pathInfo);
    }

    private void shortCircuitFaviconOrRobotsRequest(String pathInfo, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String hostname = request.getServerName().toLowerCase();
        SiteConfig config = configDai.getConfig(hostname);
        Path siteRoot = contentRoot.toPath().resolve(config.getSiteName());
        if (FAVICON_URL.equals(pathInfo)) {
            handleFaviconRequest(siteRoot, response);
        } else {
            handleRobotsTxtRequest(siteRoot, response);
        }
    }

    private void handleFaviconRequest(Path siteRoot, HttpServletResponse response) throws IOException {
        Path robotsFile = siteRoot.resolve(FAVICON);
        if (Files.exists(robotsFile)) {
            response.setContentType("image/x-icon");
            ServletUtils.writeBinary(response, robotsFile);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleRobotsTxtRequest(Path siteRoot, HttpServletResponse response) throws IOException {
        Path robotsFile = siteRoot.resolve(ROBOTS_TXT);
        if (Files.exists(robotsFile)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain; charset=UTF-8");
            ServletUtils.writeText(response, robotsFile.toFile());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void writeUnavaliablePage(HttpServletResponse response) throws ServletException {
        // not tailored to visited module or website
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        try {
            PrintWriter out = response.getWriter();
            out.write(GLOBAL_UNAVAILABLE_PAGE);
            out.flush();
        } catch (IOException e) {
            throw new ServletException("Cannot write unavailable page to HttpServletResponse", e);
        }
    }

    @Override
    public String getServletInfo() {
        return "SiteServant";
    }

    @Override
    public void destroy() {
        logger.info("Destroying servlet ".concat(getServletInfo()));
    }

}
