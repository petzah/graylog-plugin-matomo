package uk.co.znik.graylog.plugins.mamoto;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Collection;
import java.util.Collections;

public class MamotoPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new MamotoMetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Collections.<PluginModule>singletonList(new MamotoModule());
    }
}
