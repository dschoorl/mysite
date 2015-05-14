package info.rsdev.mysite.common;

import info.rsdev.mysite.common.domain.MenuGroup;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface defines the transfer point of a request from the servlet to the module
 */
public interface RequestHandler {
    
    /**
     * Handle the request and write the response to the {@link HttpServletResponse}.
     * 
     * @param config
     * @param menu
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void handle(ModuleConfig config, List<MenuGroup> menu, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
    
    /**
     * Get the menu items that this module will contribute to the webpage's navigation menu. When the module does
     * not contribute any menuitems, it should return null.
     * 
     * @param config
     * @return
     */
    public MenuGroup getMenuItems(ModuleConfig config);
}
