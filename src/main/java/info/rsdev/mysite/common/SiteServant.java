package info.rsdev.mysite.common;

import java.io.IOException;

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
    
    private ServletConfig servletConfig  = null;
    
    private ConfigDAI configDai = null;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        //Currently this servlet is not configurable -- this may change in the future
        logger.info(String.format("Initializing Servlet %s", getClass().getName()));
        this.servletConfig = config;
        this.configDai = new FileConfigDAO();
    }
    
    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }
    
    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        String hostname = request.getServerName().toLowerCase();
        SiteConfig config = configDai.getConfig(hostname);
        
        //get path into module context and then get page for path from the appropriate module
        String modulePath = ((HttpServletRequest)request).getPathInfo();
        if (modulePath == null) {
            modulePath = "/";
        }
        ModuleConfig moduleConfig = config.getModuleConfig(modulePath);
        if (moduleConfig == null) {
            //TODO: return 404 page
            throw new IllegalStateException(String.format("Page not found: %s", ((HttpServletRequest)request).getRequestURL()));
        }
        moduleConfig.getRequestHandler().handle(moduleConfig, (HttpServletRequest)request, (HttpServletResponse)response);
        
        //TODO: write entry to access logfile
        
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
