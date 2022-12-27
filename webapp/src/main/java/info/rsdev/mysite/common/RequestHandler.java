package info.rsdev.mysite.common;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import info.rsdev.mysite.common.domain.MenuGroup;
import info.rsdev.mysite.common.domain.accesslog.ModuleHandlerResult;

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
     * @return identifier for the content served, for logging purposes
     * @throws ServletException
     * @throws IOException
     */
    public ModuleHandlerResult handle(ModuleConfig config, List<MenuGroup> menu, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
    
    /**
     * Get the menu items that this module will contribute to the webpage's navigation menu. When the module does
     * not contribute any menuitems, it should return null.
     * 
     * @param config
     * @return
     */
    public MenuGroup getMenuItems(ModuleConfig config);
}
