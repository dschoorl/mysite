package info.rsdev.mysite.common;

import info.rsdev.mysite.common.domain.AccessLogEntryV1;
import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.util.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteServant implements Servlet {
    
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
    
    private ServletConfig servletConfig  = null;
    
    private ConfigDAI configDai = null;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        //Currently this servlet is not configurable -- this may change in the future
        logger.info(String.format("Initializing Servlet %s", getClass().getName()));
        this.servletConfig = config;
        this.configDai = new FileConfigDAO(getContextPath(config));
        logger.info(String.format("Platform default encoding: %s", Charset.defaultCharset()));
    }
    
    protected String getContextPath(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        String servletContextPath = servletContext.getContextPath();
        if (servletContextPath.isEmpty()) {
            servletContextPath = "/";
        }
        
        ServletRegistration registration = servletContext.getServletRegistration(config.getServletName());
        Collection<String> mappings = registration.getMappings();
        if (mappings.size() > 1) {
            throw new ServletException(String.format("%s must only be mapped to a single url pattern: %s",
                    getClass().getName(), mappings));
        }
        if (mappings.isEmpty()) {
            return getContextPath(servletContextPath, null);
        }
        return getContextPath(servletContextPath, mappings.iterator().next());
    }
    
    protected String getContextPath(String servletContextPath, String urlMapping) {
        if (urlMapping != null) {
            if (urlMapping.endsWith("/*")) {
                urlMapping = urlMapping.substring(0, urlMapping.length() - 1);  //remove trailing astrix
            } else if (!urlMapping.endsWith("/")){
                //remove possible resource name mapping
                String lastPart = urlMapping;
                int slashIndex = urlMapping.lastIndexOf('/');
                if (slashIndex > 1) {
                    lastPart = urlMapping.substring(slashIndex);
                }
                if (lastPart.contains("*.") || lastPart.contains(".")) {
                    urlMapping = urlMapping.substring(0, slashIndex + 1);   //include the forward slash
                } else {
                    urlMapping = urlMapping.concat("/");
                }
            }
        }
        
        return ServletUtils.concatenatePaths(servletContextPath, urlMapping);
    }
    
    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }
    
    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        String contentId = null;
        AccessLogEntryV1 logEntry = new AccessLogEntryV1().feedRequest((HttpServletRequest)request);
        ModuleConfig moduleConfig = null;
        try {
            String modulePath = ((HttpServletRequest)request).getPathInfo();
            if (modulePath == null) {
                modulePath = "/";
            }
            logger.debug(String.format("Received request: %s", modulePath));
            
            String hostname = request.getServerName().toLowerCase();
            SiteConfig config = configDai.getConfig(hostname);
            
            //get path into module context and then get page for path from the appropriate module
            moduleConfig = config.getModuleConfig(modulePath);
            if (moduleConfig == null) {
                logger.error(String.format("No module configered to serve request '%s'", modulePath));
                ((HttpServletResponse)response).sendError(404);
                return;
            }
            if (moduleConfig.isDisabled()) {
                logger.info(String.format("%s is disabled for %s; serving unavailable page", moduleConfig, hostname));
                writeUnavaliablePage((HttpServletResponse)response);
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
            Logger accessLogger = null;
            if ((moduleConfig == null) || (moduleConfig.getAccessLogger() == null)) {
                accessLogger = GLOBAL_ACCESS_LOGGER;
            } else {
                accessLogger = moduleConfig.getAccessLogger();
            }
            accessLogger.info(logEntry.markFinished(contentId, ((HttpServletResponse)response).getStatus()).toString());
        }
    }
    
    protected void writeUnavaliablePage(HttpServletResponse response) throws ServletException {
        //not tailored to visited module or website
        response.setContentType("text/html");
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
