package info.rsdev.mysite.common;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interface defines the transfer point of a request from the servlet to the module
 */
public interface RequestHandler {
    
    public void handle(ModuleConfig config, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
    
}
