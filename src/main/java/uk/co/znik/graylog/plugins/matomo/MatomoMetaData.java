package uk.co.znik.graylog.plugins.matomo;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;


public class MatomoMetaData implements PluginMetaData{

    private static final String PLUGIN_PROPERTIES = "graylog-plugin-matomo/graylog-plugin.properties";

    @Override
    public String getUniqueId() {
        return "uk.co.znik.graylog.plugins.MatomoPlugin";
    }

    @Override
    public String getName() {
        return "Matomo plugin";
    }

    @Override
    public String getAuthor() {
        return "Peter Zahradnik";
    }

    @Override
    public URI getURL() {
        return URI.create("https://github.com/petzah/graylog-plugin-matomo");
    }

    @Override
    public Version getVersion() {
        return Version.fromPluginProperties(this.getClass(), PLUGIN_PROPERTIES, "version", Version.from(0, 0, 0, "unknown"));
    }
    @Override
    public String getDescription() {
        return "Write tacking info to Matomo instance.";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.fromPluginProperties(this.getClass(), PLUGIN_PROPERTIES, "graylog.version", Version.CURRENT_CLASSPATH);
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.singleton(ServerStatus.Capability.SERVER);
    }
}
