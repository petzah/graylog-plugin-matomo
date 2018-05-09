package uk.co.znik.graylog.plugins.mamoto;

import org.graylog2.plugin.PluginModule;

public class MamotoModule extends PluginModule {

    @Override
    protected void configure() {
        addMessageOutput(MamotoOutput.class);
        addConfigBeans();
    }
}
