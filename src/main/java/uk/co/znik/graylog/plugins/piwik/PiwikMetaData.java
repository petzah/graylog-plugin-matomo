package uk.co.znik.graylog.plugins.piwik;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;


public class PiwikMetaData implements PluginMetaData{

    private static final String PLUGIN_PROPERTIES = "org.graylog.plugins.graylog-plugin-piwik/graylog-plugin.properties";

    @Override
    public String getUniqueId() {
        return "uk.co.znik.graylog.plugins.Piwik";
    }

    @Override
    public String getName() {
        return "Piwik plugin";
    }

    @Override
    public String getAuthor() {
        return "Peter Zahradnik";
    }

    @Override
    public URI getURL() {
        return URI.create("https://gitlab.com/petzah/graylog-plugin-piwik");
    }

    @Override
    public Version getVersion() {
        return Version.fromPluginProperties(this.getClass(), PLUGIN_PROPERTIES, "version", Version.from(0, 0, 1, "unknown"));
    }
    @Override
    public String getDescription() {
        return "Writes messages to Piwik installation.";
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
