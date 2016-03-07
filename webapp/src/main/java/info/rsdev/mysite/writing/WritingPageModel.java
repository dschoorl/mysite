package info.rsdev.mysite.writing;

import info.rsdev.mysite.common.domain.BasicPageModel;

/**
 * The information available to templates to generate the html page of the photo gallery. This is a DTO (Data 
 * Transfer Object).
 */
public class WritingPageModel extends BasicPageModel<WritingModuleConfig>{
    
    public WritingPageModel(WritingModuleConfig config, String imageGroup) {
        super(config, imageGroup);
    }

}
