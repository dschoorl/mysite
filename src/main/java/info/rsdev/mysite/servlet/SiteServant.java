package info.rsdev.mysite.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SiteServant implements Servlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SiteServant.class);
    
    private ServletConfig config  = null;
    
    private final Map<String, File> contextLocationByAlias = new HashMap<>();
    
    private File contentRoot = null;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        //Currently this servlet is not configurable -- this may change in the future
        logger.info(String.format("Initializing Servlet %s", getClass().getName()));
        this.config = config;
        String webinfDir = config.getServletContext().getRealPath("/WEB-INF");
        contentRoot = new File(new File(webinfDir, "classes"), "sites");
        File aliasesFile = new File(contentRoot, "aliases.properties");
        Properties aliases = new Properties();
        try (FileReader reader = new FileReader(aliasesFile)) {
            aliases.load(reader);
        } catch (IOException e) {
            logger.error(String.format("Problem loading site aliases from %s", aliasesFile), e);
        }
        
        //verify that the target directories from the mappings exist
        for (Entry<Object, Object> aliasMapping: aliases.entrySet()) {
            String alias = (String)aliasMapping.getKey();
            String subdirectory = (String)aliasMapping.getValue();
            File location = new File(contentRoot, subdirectory);
            if (!location.isDirectory()) {
                logger.warn(String.format("Wrong entry in %s: Location %s (relative to contentRoot %s) for alias %s does not exist",
                        aliasesFile, subdirectory, contentRoot, alias));
                continue;
            }
            if (this.contextLocationByAlias.containsKey(alias)) {
                logger.warn(String.format("Alias %s is defined multiple times in %s: using new value %s, dropping previous value %s",
                        alias, aliasesFile, subdirectory, this.contextLocationByAlias.get(alias).getName()));
            }
            this.contextLocationByAlias.put(alias, location);
        }
    }
    
    @Override
    public ServletConfig getServletConfig() {
        return config;
    }
    
    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        String hostname = request.getServerName();
        File siteLocation = new File(contentRoot, hostname);
        if (!siteLocation.isDirectory()) {
            if (this.contextLocationByAlias.containsKey(hostname)) {
                siteLocation = this.contextLocationByAlias.get(hostname);
            } else {
                String message = String.format("No site directory found for '%s'", hostname);
//                logger.error(message);
                throw new ServletException(message);
            }
        }
        File indexFile = new File(siteLocation, "index.html");
        if (!indexFile.isFile()) {
            throw new ServletException("No index file present for this site");
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try (BufferedReader reader = new BufferedReader(new FileReader(indexFile), 2048)) {
            char[] buffer = new char[2048];
            while (reader.read(buffer) != -1) {
                out.write(buffer);
            }
            out.flush();
        } catch (IOException e) {
            logger.error("Error reading index.html file", e);
            out.write("...OMG. Something went horribly wrong while generating this page for you. Sorry. ");
            out.write("Let us know if the error keeps occuring.");
        }
    }
    
    @Override
    public String getServletInfo() {
        return "SiteServant";
    }
    
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        
    }
    
}
