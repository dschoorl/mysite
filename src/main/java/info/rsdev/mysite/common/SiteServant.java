package info.rsdev.mysite.common;

import info.rsdev.mysite.common.domain.AccessLogEntry;
import info.rsdev.mysite.common.domain.MenuGroup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteServant implements Servlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SiteServant.class);
    private static final Logger ACCESS_LOGGER = LoggerFactory.getLogger("AccessLog");
    
    private ServletConfig servletConfig  = null;
    
    private ConfigDAI configDai = null;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        //Currently this servlet is not configurable -- this may change in the future
        logger.info(String.format("Initializing Servlet %s", getClass().getName()));
        this.servletConfig = config;
        this.configDai = new FileConfigDAO(config.getServletContext().getContextPath());
        logger.info(String.format("Platform default encoding: %s", Charset.defaultCharset()));
    }
    
    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }
    
    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        String contentId = null;
        AccessLogEntry logEntry = new AccessLogEntry().feedRequest((HttpServletRequest)request);
        try {
            String modulePath = ((HttpServletRequest)request).getServletPath();
            logger.debug("Received request: ".concat((modulePath)));
            
            String hostname = request.getServerName().toLowerCase();
            SiteConfig config = configDai.getConfig(hostname);
            
            //get path into module context and then get page for path from the appropriate module
            ModuleConfig moduleConfig = config.getModuleConfig(modulePath);
            if (moduleConfig == null) {
                logger.error(String.format("No module configered to serve request ".concat(modulePath)));
                ((HttpServletResponse)response).sendError(404);
                return;
            }
            logEntry.feedModuleConfig(moduleConfig);
            if (logger.isDebugEnabled()) {
                HttpServletRequest r = (HttpServletRequest)request;
                logger.debug(String.format("%s will be serving %s request: %s [QuesryString=%s]", moduleConfig.getRequestHandler(), 
                        r.getMethod(), r.getServletPath(), r.getQueryString()));
            }
            List<MenuGroup> menu = config.getMenu();
            contentId = moduleConfig.getRequestHandler().handle(moduleConfig, menu, (HttpServletRequest)request, (HttpServletResponse)response);
        } catch (RuntimeException e) {
            ((HttpServletResponse)response).setStatus(500);
            throw e;
        } finally {
            //write entry to access logfile
            ACCESS_LOGGER.info(logEntry.markFinished(contentId, ((HttpServletResponse)response).getStatus()).toString());
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
