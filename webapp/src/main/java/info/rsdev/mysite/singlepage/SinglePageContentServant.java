package info.rsdev.mysite.singlepage;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.common.domain.DefaultMenuGroup;
import info.rsdev.mysite.common.domain.DefaultMenuItem;
import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.MenuItem;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;
import info.rsdev.mysite.exception.ConfigurationException;
import info.rsdev.mysite.singlepage.domain.SinglePage;
import info.rsdev.mysite.singlepage.domain.SinglePageCollection;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.stringtemplate.v4.ST;

public class SinglePageContentServant implements RequestHandler, ConfigKeys {
    
    private final SinglePageCollection pageCollection;
    
    public SinglePageContentServant(File siteDir, String collectionPath, String mountPoint) {
        this.pageCollection = new SinglePageCollection(siteDir, collectionPath, mountPoint);
    }
    
    @Override
    public ModuleHandlerResult handle(ModuleConfig config, List<MenuGroup> menu, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (config == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(config instanceof SinglePageModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s. please check"
                    + "the value of property '%s'", SinglePageModuleConfig.class.getSimpleName(), config, MODULECONFIGTYPE_KEY));
        }
        SinglePageModuleConfig singlepageConfig = (SinglePageModuleConfig) config;
        String pageName = getPageName(singlepageConfig, request);
        SinglePage content = this.pageCollection.getPage(pageName);
        SinglePageModel model = new SinglePageModel(singlepageConfig, content);
        model.setMenu(menu);
        
        String templateName = renderPage(response, model);
        return new ModuleHandlerResult(templateName, pageName);
    }
    
    private String renderPage(HttpServletResponse response, SinglePageModel pageModel) throws ServletException {
        SinglePageModuleConfig singlepageConfig = pageModel.getConfig();
        ST template = singlepageConfig.getTemplate(pageModel.getSelectedMenuItemName());
        try {
            if (template == null) {
                response.sendError(404);
            } else {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");
                template.add("model", pageModel);
//                template.add("locale", singlepageConfig.getLocale().getLanguage());
                response.getWriter().write(template.render());
            }
        } catch (IOException e) {
            throw new ServletException("Error occured during preparation of web page", e);
        }
        return template==null?null:template.getName();
    }
    
    private String getPageName(SinglePageModuleConfig singlepageConfig, HttpServletRequest request) {
        //the pagename is the name that follows the mountpoint in the http request path
        String path = request.getPathInfo();
        int index = path.lastIndexOf(singlepageConfig.getMountPoint());
        if (index >= 0) {
            try {
                return URLDecoder.decode(path.substring(index + singlepageConfig.getMountPoint().length() + 1), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public MenuGroup getMenuItems(ModuleConfig config) {
        // filtering not yet implemented
        List<SinglePage> pages = this.pageCollection.getPages();
        List<MenuItem> menuItems = new ArrayList<>(pages.size());
        for (SinglePage page: pages) {
            try {
                String pageName = page.getName();
                String targetUrl = String.format("%s%s/%s", 
                        config.getContextPath(), 
                        config.getMountPoint(),
                        URLEncoder.encode(pageName, "UTF-8"));
                menuItems.add(new DefaultMenuItem(pageName, targetUrl));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Could not URLEncode the pageName with UTF-8", e);
            }
        }
        return new DefaultMenuGroup(menuItems, null, 1);
    }
    
}
