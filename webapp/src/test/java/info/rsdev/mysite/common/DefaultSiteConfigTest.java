package info.rsdev.mysite.common;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DefaultSiteConfigTest {
    
    private DefaultSiteConfig subjectUnderTest = null;
    
    @Before
    public void setup() {
        subjectUnderTest = new DefaultSiteConfig("test", Collections.emptyMap());
    }

    @Test
    public void selectModuleWithLongestMountPointWhenMultipleCandidates() {
        List<ModuleConfig> candidates = new LinkedList<>();
        candidates.add(createMockForMountPoint(""));
        candidates.add(createMockForMountPoint("static"));
        ModuleConfig bestFit = subjectUnderTest.selectBestFit(candidates);
        assertEquals("static", bestFit.getMountPoint());
    }
    
    @Test
    public void selectSingleModule() {
        List<ModuleConfig> candidates = Collections.singletonList(mock(ModuleConfig.class));
        assertNotNull(subjectUnderTest.selectBestFit(candidates));
    }
    
    @Test
    public void selectNothingWhenThereAreNoCandidates() {
        assertNull(subjectUnderTest.selectBestFit(Collections.emptyList()));
    }
    
    private ModuleConfig createMockForMountPoint(String mountPoint) {
        ModuleConfig config = mock(ModuleConfig.class);
        when(config.getMountPoint()).thenReturn(mountPoint);
        return config;
    }

}
