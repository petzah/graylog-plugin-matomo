package uk.co.znik.graylog.plugins.matomo;

import org.graylog2.plugin.PluginModule;

public class MatomoModule extends PluginModule {

    @Override
    protected void configure() {
        addMessageOutput(MatomoOutput.class);
        addConfigBeans();
    }
}
