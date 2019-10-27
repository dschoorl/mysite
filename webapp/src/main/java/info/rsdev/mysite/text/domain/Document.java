package info.rsdev.mysite.text.domain;

import java.time.LocalDate;

import info.rsdev.mysite.common.domain.resources.PathResource;

public interface Document extends PathResource {
    
    LocalDate getDateCreated();
    
    LocalDate getDateChanged();

}
