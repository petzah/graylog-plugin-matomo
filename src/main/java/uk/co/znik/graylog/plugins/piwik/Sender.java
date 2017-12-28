package uk.co.znik.graylog.plugins.piwik;

import org.graylog2.plugin.Message;

public interface Sender {
    void initialize();
    void stop();
    void sendMessage(Message message);
    boolean isInitialized();
}
