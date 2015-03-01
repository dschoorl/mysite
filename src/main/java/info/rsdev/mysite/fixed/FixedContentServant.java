package info.rsdev.mysite.fixed;

import info.rsdev.mysite.common.ModuleConfig;
import info.rsdev.mysite.common.RequestHandler;
import info.rsdev.mysite.exception.ConfigurationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedContentServant implements RequestHandler, ConfigKeys {
    
    private static final Logger logger = LoggerFactory.getLogger(FixedContentServant.class);
    
    private final FixedContentModuleConfig config;
    
    public FixedContentServant(FixedContentModuleConfig moduleConfig) {
        this.config = moduleConfig;
    }
    
    @Override
    public void handle(ModuleConfig config, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (config == null) {
            throw new ConfigurationException(String.format("%s cannot be null", ModuleConfig.class.getSimpleName()));
        }
        if (!(config instanceof FixedContentModuleConfig)) {
            throw new ConfigurationException(String.format("Expected was config of type %, but encountered was %s", 
                FixedContentModuleConfig.class.getSimpleName(), config));
        }
        FixedContentModuleConfig fixedConfig = (FixedContentModuleConfig)config;
        Path resourceLocation = fixedConfig.getSiteRoot();
        if (request.getPathInfo() != null) {
            resourceLocation = resourceLocation.resolve(request.getPathInfo());
        }
        if (resourceLocation.toFile().isDirectory()) {
            resourceLocation = resourceLocation.resolve("index.html");
        }
        if (!resourceLocation.toFile().isFile()) {
            response.sendError(404, String.format("Resource %s does not exist", resourceLocation));
            return;
        }
        //TODO: resolve content type (how?)
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try (BufferedReader reader = new BufferedReader(new FileReader(resourceLocation.toFile()), 2048)) {
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
    
}
