package info.rsdev.mysite.writing.dao;

import java.io.File;

import javax.inject.Inject;

import info.rsdev.mysite.common.startup.PropertiesModule.ContentRoot;
import info.rsdev.mysite.common.startup.PropertiesModule.ContextPath;
import info.rsdev.mysite.writing.domain.Document;

public class FileReadingDao implements IReadingDao {

    @Inject
    public FileReadingDao(@ContentRoot File contentRoot, @ContextPath String contextPath) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Document getDocumentByName(String name) {
        return null;
    }

}
