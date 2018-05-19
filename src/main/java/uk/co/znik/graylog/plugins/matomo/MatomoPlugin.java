package uk.co.znik.graylog.plugins.matomo;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Collection;
import java.util.Collections;

public class MatomoPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new MatomoMetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Collections.<PluginModule>singletonList(new MatomoModule());
    }
}
