package info.rsdev.mysite;

import javax.servlet.DispatcherType;

import com.google.inject.servlet.GuiceFilter;

import info.rsdev.mysite.common.startup.GuiceServletConfig;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

/**
 * This class is the entry-point of the application when it is run as an
 * executable war file. It will start the embedded Undertow servlet container to
 * serve the web pages. Both deploying the application into a servlet container
 * as well as running as executable war are supported, but the application is
 * only tested with deployment in Tomcat 9 server.
 */
public class MySiteStarter {

    private static final String GUICE_FILTER_NAME = "guiceFilter";

    public static void main(String[] args) throws Exception {

        /*
         * Next to Undertow, I have tried embedding Jetty and Tomcat as wel. I
         * found configuring Tomcat too cumbersome, and embedding Jetty conflicted
         * with deploying the war file in standalone Tomcat server.
         */
        startUndertow();
    }

    private static void startUndertow() throws Exception {
        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(MySiteStarter.class.getClassLoader())
                .setContextPath("/")
                .setDeploymentName("MySite")
                .addListener(Servlets.listener(GuiceServletConfig.class))
                .addFilter(Servlets.filter(GUICE_FILTER_NAME, GuiceFilter.class))
                .addFilterUrlMapping(GUICE_FILTER_NAME, "/*", DispatcherType.REQUEST)
                .setEagerFilterInit(true);

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        PathHandler path = Handlers.path(Handlers.path())
                .addPrefixPath("/", manager.start());

        Undertow server = Undertow.builder()
                .addHttpListener(9080, "0.0.0.0")
                .setHandler(path)
                .build();
        server.start();
    }

}
