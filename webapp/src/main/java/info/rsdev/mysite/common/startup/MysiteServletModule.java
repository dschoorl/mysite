package info.rsdev.mysite.common.startup;

import com.google.inject.servlet.ServletModule;

import info.rsdev.mysite.common.ConfigDAI;
import info.rsdev.mysite.common.FileConfigDAO;
import info.rsdev.mysite.common.SiteServant;
import info.rsdev.mysite.writing.dao.FileReadingDao;
import info.rsdev.mysite.writing.dao.IReadingDao;

public class MysiteServletModule extends ServletModule {

    @Override
    protected void configureServlets() {
        serve("/*").with(SiteServant.class);
        
        bind(ConfigDAI.class).to(FileConfigDAO.class);
        bind(IReadingDao.class).to(FileReadingDao.class);
    }
}
