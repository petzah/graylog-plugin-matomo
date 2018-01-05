package uk.co.znik.graylog.plugins.piwik;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.BooleanField;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.inputs.annotations.ConfigClass;
import org.graylog2.plugin.inputs.annotations.FactoryClass;
import org.graylog2.plugin.outputs.MessageOutput;
import org.graylog2.plugin.outputs.MessageOutputConfigurationException;
import org.graylog2.plugin.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class PiwikOutput implements MessageOutput {

    private static final Logger LOG = LoggerFactory.getLogger(PiwikOutput.class);

    private static final String PIWIK_URL = "piwik_url";
    private static final String PIWIK_TOKEN = "piwik_token";
    private static final String PIWIK_SITE_CREATE = "piwik_site_create";

    private PiwikInstance piwikInstance;
    private boolean running;
    private final Configuration configuration;

    @Inject
    public PiwikOutput(@Assisted Configuration c) throws MessageOutputConfigurationException {
        if (!checkConfiguration(c)) {
            throw new MessageOutputConfigurationException("Missing or incorrect configuration.");
        }
        configuration = c;
        piwikInstance = new PiwikInstance(c.getString(PIWIK_URL), c.getString(PIWIK_TOKEN));
        running = true;
    }

    private boolean checkConfiguration(Configuration c) {
        return c.stringIsSet(PIWIK_URL)
                && c.stringIsSet(PIWIK_TOKEN);
    }

    public boolean isRunning() {
        return running;
    }

    public void write(Message message) throws Exception {
        //LOG.warn("DEBUG: message is: " + message);
        String host = (String) message.getField("host");
        // we don't have host/site which is required, skip this message
        if (host == null)
            return;
        // http or https?
        String request_scheme = (String) message.getField("request_scheme");
        PiwikSite piwikSite = piwikInstance.getSite(host);
        //LOG.warn("DEBUG: got site with name: " + piwikSite + " from " + piwikInstance);
        if (piwikSite == null && configuration.getBoolean(PIWIK_SITE_CREATE)) {
            piwikSite = piwikInstance.addNewSite(host, request_scheme+"://"+host);
        }

        //sender.sendMessage(message);
    }

    public void write(List<Message> messages) throws Exception {


    }

    public void stop() {
        //sender.stop()
        running = false;
    }

    @FactoryClass
    public interface Factory extends MessageOutput.Factory<PiwikOutput> {

        PiwikOutput create(Stream stream, Configuration configuration);

        Config getConfig();

        Descriptor getDescriptor();
    }

    @ConfigClass
    public static class Config extends MessageOutput.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final ConfigurationRequest configurationRequest = new ConfigurationRequest();

            configurationRequest.addField(new TextField(
                    PIWIK_URL, "Piwik URI", "",
                    "HTTP address of piwik installation",
                    ConfigurationField.Optional.NOT_OPTIONAL)
            );
            configurationRequest.addField(new TextField(
                    PIWIK_TOKEN, "Piwik Token", "",
                    "Piwik user token to access API",
                    ConfigurationField.Optional.NOT_OPTIONAL)
            );
            configurationRequest.addField(new BooleanField(
                    PIWIK_SITE_CREATE, "Create sites", false,
                    "Create sites in piwik installation if not exist from $HTTP_HOST.")
            );
            return configurationRequest;
        }
    }

    public static class Descriptor extends MessageOutput.Descriptor {
        public Descriptor() {
            super("Piwik Output", false, "",
                    "Writes messages to your Piwik installation via it's API.");
        }
    }

}
