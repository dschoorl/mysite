package info.rsdev.mysite.common.startup;

import com.google.inject.servlet.ServletModule;

import info.rsdev.mysite.common.SiteServant;

public class MysiteServletModule extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/*").with(SiteServant.class);
        
//        filter("/*").through(PersistFilter.class);  //start JPA and provide a session per http request
    }
}
