package uk.co.znik.graylog.plugins.mamoto;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.apache.commons.codec.digest.DigestUtils;
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
import org.piwik.java.tracking.PiwikRequest;
import org.piwik.java.tracking.PiwikTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;


public class MamotoOutput implements MessageOutput {

    private static final Logger LOG = LoggerFactory.getLogger(MamotoOutput.class);

    private static final String PIWIK_URL = "mamoto_url";
    private static final String PIWIK_TOKEN = "mamoto_token";
    private static final String PIWIK_SITE_CREATE = "mamoto_site_create";

    private MamotoInstance mamotoInstance;
    private PiwikTracker mamotoTracker;
    private boolean running;
    private final Configuration configuration;

    @Inject
    public MamotoOutput(@Assisted Configuration c) throws MessageOutputConfigurationException {
        if (!checkConfiguration(c)) {
            throw new MessageOutputConfigurationException("Missing or incorrect configuration.");
        }
        configuration = c;
        mamotoInstance = new MamotoInstance(c.getString(PIWIK_URL), c.getString(PIWIK_TOKEN));
        mamotoTracker = new PiwikTracker(c.getString(PIWIK_URL)+"/mamoto.php");
        running = true;
    }

    private boolean checkConfiguration(Configuration c) {
        return c.stringIsSet(PIWIK_URL)
                && c.stringIsSet(PIWIK_TOKEN);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void write(Message message) throws Exception {
        //LOG.warn("DEBUG: message is: " + message);
        String request_scheme = (String) message.getField("request_scheme")+"://";
        String host = (String) message.getField("host");
        String request_uri = (String) message.getField("request_uri");
        String remote_addr = (String) message.getField("remote_addr");
        String http_user_agent = (String) message.getField("http_user_agent");

        String visitorId = DigestUtils.sha1Hex(remote_addr+http_user_agent).substring(0,16);

        // we don't have host/site or request_uri which is required, skip this message
        if ((host == null) || (request_uri == null) || (request_scheme == null))
            return;
        // http or https?
        MamotoSite mamotoSite = mamotoInstance.getSite(host);
        if (mamotoSite == null && configuration.getBoolean(PIWIK_SITE_CREATE)) {
            mamotoSite = mamotoInstance.addNewSite(host, request_scheme+host);
        }

        URL actionUrl = new URL(request_scheme+host+request_uri);

        PiwikRequest piwikRequest = new PiwikRequest(mamotoSite.getIdsite(), actionUrl);
        piwikRequest.setAuthToken(configuration.getString(PIWIK_TOKEN)); // must be first

        piwikRequest.setVisitorId(visitorId);
        piwikRequest.setVisitorIp(remote_addr);
        piwikRequest.setHeaderUserAgent(http_user_agent);

        mamotoTracker.sendRequest(piwikRequest);

        //sender.sendMessage(message);
    }

    @Override
    public void write(List<Message> messages) throws Exception {


    }

    @Override
    public void stop() {
        //sender.stop()
        running = false;
    }

    @FactoryClass
    public interface Factory extends MessageOutput.Factory<MamotoOutput> {

        @Override
        MamotoOutput create(Stream stream, Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    @ConfigClass
    public static class Config extends MessageOutput.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final ConfigurationRequest configurationRequest = new ConfigurationRequest();

            configurationRequest.addField(new TextField(
                    PIWIK_URL, "Piwik URI", "",
                    "HTTP address of mamoto installation",
                    ConfigurationField.Optional.NOT_OPTIONAL)
            );
            configurationRequest.addField(new TextField(
                    PIWIK_TOKEN, "Piwik Token", "",
                    "Piwik user token to access API",
                    ConfigurationField.Optional.NOT_OPTIONAL)
            );
            configurationRequest.addField(new BooleanField(
                    PIWIK_SITE_CREATE, "Create sites", false,
                    "Create sites in mamoto installation if not exist from $HTTP_HOST.")
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
