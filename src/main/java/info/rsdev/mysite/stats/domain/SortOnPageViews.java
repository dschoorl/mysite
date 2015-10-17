package info.rsdev.mysite.stats.domain;

import java.io.Serializable;
import java.util.Comparator;

public class SortOnPageViews implements Comparator<VisitorsAndPageViews<?>>, Serializable {

    private static final long serialVersionUID = 1L;
    
    public static final SortOnPageViews INSTANCE = new SortOnPageViews();

    @Override
    public int compare(VisitorsAndPageViews<?> o1, VisitorsAndPageViews<?> o2) {
        if ((o1 == null) && (o2 == null)) { return 0; }
        if (o1 == null) { return -1; }
        if (o2 == null) { return 1; }
        return -1 * Integer.compare(o1.getPageViews(), o2.getPageViews());
    }
    
}
