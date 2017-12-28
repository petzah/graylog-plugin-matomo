package uk.co.znik.graylog.plugins.piwik;

import org.graylog2.plugin.PluginModule;

public class PiwikModule extends PluginModule {

    @Override
    protected void configure() {
        addMessageOutput(PiwikOutput.class);
        addConfigBeans();
    }
}
