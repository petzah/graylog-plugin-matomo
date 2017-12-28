package uk.co.znik.graylog.plugins.piwik;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Collection;
import java.util.Collections;

public class PiwikPlugin implements Plugin {
    public PluginMetaData metadata() {
        return new PiwikMetaData();
    }

    public Collection<PluginModule> modules () {
        return Collections.<PluginModule>singletonList(new PiwikModule());
    }
}
