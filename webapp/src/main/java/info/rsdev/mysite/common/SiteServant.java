package info.rsdev.mysite.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.accesslog.AccessLogEntryV1;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;
import info.rsdev.mysite.exception.ConfigurationException;

@Singleton
public class SiteServant extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(SiteServant.class);
    private static final Logger GLOBAL_ACCESS_LOGGER = LoggerFactory.getLogger("AccessLog");
    private static final String GLOBAL_UNAVAILABLE_PAGE =
            "<!DOCTYPE html>\n" +
                    " <head>\n" +
                    "  <style>\n" +
                    "    body {\n" +
                    "        background: grey;\n" +
                    "        font-family: Arial;\n" +
                    "        font-size: 36px }" +
                    "    section {\n" +
                    "        background: grey;\n" +
                    "        color: black;\n" +
                    "        border-radius: 1em;\n" +
                    "        padding: 1em;\n" +
                    "        position: absolute;\n" +
                    "        top: 50%;\n" +
                    "        left: 50%;\n" +
                    "        margin-right: -50%;\n" +
                    "        transform: translate(-50%, -50%) }\n" +
                    "  </style></head>\n" +
                    "  <section>\n" +
                    "\n" +
                    "  <p>TEMPORARILY<br />&nbsp;UNAVAILABLE</p>\n" +
                    "\n" +
                    "  </section>\n";

    private ConfigDAI configDai = null;

    @Inject
    public SiteServant(ConfigDAI configDao) {
        this.configDai = configDao;
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
        ModuleHandlerResult result = null;
        AccessLogEntryV1 logEntry = new AccessLogEntryV1().feedRequest(request);
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
                logger.error(String.format("No module configered to serve module path '%s' for %s", modulePath, hostname));
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
            List<MenuGroup> menu = config.getMenu();
            result = moduleConfig.getRequestHandler().handle(moduleConfig, menu, (HttpServletRequest) request,
                    (HttpServletResponse) response);
            
            if (!result.isAlreadyHandled() && result.equals(ModuleHandlerResult.NO_CONTENT)) {
                response.setStatus(404);
            }
        } catch (ConfigurationException e) {
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            httpResponse.setStatus(500);
            httpResponse.getWriter().write(e.getLocalizedMessage());
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
