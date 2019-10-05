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

public class MySiteStarter {

    private static final String GUICE_FILTER_NAME = "guiceFilter";

    public static void main(String[] args) throws Exception {
        startUndertow();
    }

    private static void startUndertow() throws Exception {
        DeploymentInfo servletBuilder = Servlets.deployment()
                .setClassLoader(MySiteStarter.class.getClassLoader())
                .setContextPath("/mysite")
                .setDeploymentName("MySite")
                .addListener(Servlets.listener(GuiceServletConfig.class))
                .addFilter(Servlets.filter(GUICE_FILTER_NAME, GuiceFilter.class))
                .addFilterUrlMapping(GUICE_FILTER_NAME, "/*", DispatcherType.REQUEST)
                .setEagerFilterInit(true);

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        PathHandler path = Handlers.path(Handlers.path())
                .addPrefixPath("/mysite", manager.start());

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(path)
                .build();
        server.start();
    }

}
