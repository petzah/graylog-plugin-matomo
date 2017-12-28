package uk.co.znik.graylog.plugins.piwik;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class PiwikMetaData implements PluginMetaData{

    public String getUniqueId() {
        return "uk.co.znik.graylog.plugins.Piwik";
    }

    public String getName() {
        return "Piwik plugin";
    }

    public String getAuthor() {
        return "Peter Zahradnik";
    }

    public URI getURL() {
        return URI.create("https://www.graylog.org/");
    }

    public Version getVersion() {
        return new Version(0,0,1);
    }

    public String getDescription() {
        return "Writes messages to Piwik installation.";
    }

    public Version getRequiredVersion() {
        return new Version(2,3,2);
    }

    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.singleton(ServerStatus.Capability.SERVER);
    }
}
